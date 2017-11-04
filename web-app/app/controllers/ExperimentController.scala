package controllers

import javax.inject.Inject

import akka.actor.{ActorPath, ActorSystem, Props}
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


@Singleton
class ExperimentController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  val experimentActor = actorSystem.actorOf(Props[ExperimentHandler], "experiment-handler")

  implicit val timeout: Timeout = 10 hours

  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[String, ExperimentState]

  def experiment(id: String) = Action.async { request =>

    (experimentActor ? GetExperiment(id)).mapTo[ExperimentState].map { experiment =>
      Ok(experiment.name)
    }

  }

  def experimentsWebSocket = WebSocket.accept[String, ExperimentState]{ request =>
    val (queueSource, futureQueue) = peekMatValue(Source.queue[ExperimentState](10, OverflowStrategy.fail))

    futureQueue.map { queue =>
      experimentActor ! GetExperiments(queue)
    }
    val in = Sink.ignore

    Flow.fromSinkAndSource(in, queueSource)
  }

  def experiments = Action {
    val (queueSource, futureQueue) = peekMatValue(Source.queue[ExperimentState](10, OverflowStrategy.fail))

    futureQueue.map { queue =>
      experimentActor ! GetExperiments(queue)
    }

    Ok.chunked(queueSource.map { tick =>
      val (prefix, author) = ("hi", "dd")
      Json.obj("message" -> s"$prefix $tick", "author" -> author).toString + "\n"
    }.limit(100))
  }

  private def peekMatValue[T, M](src: Source[T, M]): (Source[T, M], Future[M]) = {
    val p = Promise[M]
    val s = src.mapMaterializedValue { m =>
      p.trySuccess(m)
      m
    }
    (s, p.future)
  }

}
