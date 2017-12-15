package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.{ActorRef, ActorSystem, Props, _}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.cacique.jellybelly.model._
import models.com.cacique.jellybelly.model.ExperimentHandler
import play.api.libs.json.Json
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Try


object PerClientActor {
  def props(client: ActorRef, experimentHandler: ActorRef) = Props(new PerClientActor(client, experimentHandler))
}

class PerClientActor(clientActor: ActorRef, experimentHandlerActor: ActorRef) extends Actor {
  override def receive = {
    case ExperimentUpdated(experimentState) =>
      clientActor ! Json.obj("experiment" -> experimentState).toString()
    case "subscribe" =>
      experimentHandlerActor ! Subscribe(self)
      clientActor ! "subscribed"
  }

  override def postStop() = {
    println("STOPPING")
    experimentHandlerActor ! Unsubscribe(self)
  }
}

@Singleton
class ExperimentController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  implicit val timeout: Timeout = 10 hours

  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[String, ExperimentState]

  implicit val actors = actorSystem

  implicit val materializer = ActorMaterializer()

  lazy val experimentActor = actorSystem.actorOf(Props[ExperimentHandler], "experiment-handler")

  def experiment(id: String, variants: String) = Action.async { request =>

    val allVariants = variants.split(",").map { v =>
      val percentage = Try(v.split(":")(1)).getOrElse(-1).toString.toInt
      Variant(v, percentage)
    }
    (experimentActor ? GetExperiment(id, allVariants)).mapTo[ExperimentState].map { experiment =>
      Ok(experiment.name)
    }

  }

  def participate(experimentId: String, participantId: String) = Action { request =>
    (experimentActor ! Participate(experimentId, Participant(participantId)))
    Ok
  }

  def experimentsWebSocket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      PerClientActor.props(out, experimentActor)
    }
  }

}
