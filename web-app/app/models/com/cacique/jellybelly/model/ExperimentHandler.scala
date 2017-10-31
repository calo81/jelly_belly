package models.com.cacique.jellybelly.model

import akka.actor.{ActorLogging, ActorRef, Props}
import akka.persistence.PersistentActor
import com.cacique.jellybelly.model._

import scala.collection.mutable

class ExperimentHandler extends PersistentActor with ActorLogging {

  override def persistenceId = "experiment_handler"

  val experiments = mutable.Map[String, ActorRef]()

  override def receiveRecover = {
    case ExperimentCreated(experimentActor) =>
      experiments
  }

  override def receiveCommand = {
    case req@GetExperiment(id) =>
      val experiment = context.child(s"experiment_${id}").getOrElse(context.actorOf(Props(new Experiment(id, "hola", Nil)), s"experiment_${id}"))
      experiment forward req
  }

}
