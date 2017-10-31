package controllers

import javax.inject.Inject

import akka.actor.{ActorPath, ActorSystem, Props}
import com.cacique.jellybelly.model.{Experiment, ExperimentState, GetExperiment}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.iteratee._

import scala.concurrent.ExecutionContext
import akka.pattern.ask
import models.com.cacique.jellybelly.model.ExperimentHandler
import javax.inject.Singleton

import akka.util.Timeout
import play.api.Logger

import scala.concurrent.duration.Duration
import scala.concurrent.duration._


@Singleton
class ExperimentController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  val experimentActor = actorSystem.actorOf(Props[ExperimentHandler], "experiment-handler")

  implicit val timeout: Timeout = 10 hours

  def experiment(id: String) = Action.async { request =>

    (experimentActor ? GetExperiment(id)).mapTo[ExperimentState].map { experiment =>
      Ok(experiment.name)
    }

  }

  def experiments = Action.async { request =>
    val experimentsIteratee = Iteratee.foreach[ExperimentState] { experiment =>
      Logger.info(experiment.name)
    }

    experimentsIteratee.run.map { _ =>
      Ok("ff")
    }

  }
}