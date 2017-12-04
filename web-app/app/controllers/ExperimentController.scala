package controllers

import javax.inject.Inject

import akka.actor.{ActorPath, ActorRef, ActorSystem, Props}
import com.cacique.jellybelly.model._
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import play.api.libs.iteratee._

import scala.concurrent.{ExecutionContext, Future, Promise}
import akka.pattern.ask
import models.com.cacique.jellybelly.model.ExperimentHandler
import javax.inject.Singleton

import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl.{Flow, Sink, Source, SourceQueueWithComplete}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.stage.GraphStage
import akka.util.Timeout
import play.api.Logger
import play.api.libs.json.{Format, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer

import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import akka.actor._


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

    val allVariants = variants.split(",").map(v => Variant(v, 10))
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
