package models.com.cacique.jellybelly.model

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.persistence.PersistentActor
import com.cacique.jellybelly.model._
import akka.pattern.ask
import akka.stream.javadsl.SourceQueueWithComplete
import akka.stream.scaladsl.SourceQueue
import akka.util.Timeout

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


class ExperimentHandler extends Actor with ActorLogging {

  implicit val timeout: Timeout = 10 hours

  val experiments = mutable.Map[String, ActorRef]()

  val webSocketActors = mutable.Map[String, Seq[ActorRef]]("subscriptions" -> Seq())

  override def receive = {
    case req@GetExperiment(id, variants) =>
      val experiment = context.child(s"experiment_${id}").getOrElse(context.actorOf(Props(new Experiment(id, id, variants)), s"experiment_${id}"))
      experiments.put(s"experiment_${id}", experiment)
      experiment forward req
    case ev@ExperimentUpdated(_) =>
      webSocketActors.get("subscriptions").map(_.foreach(_ ! ev))
    case Subscribe(actor) =>
      webSocketActors.put("subscriptions", actor +: webSocketActors.get("subscriptions").get)
      experiments.values.foreach(_ ! PublishState)
    case p@Participate(experimentId, _) =>
      experiments.get(s"experiment_${experimentId}").foreach(_ ! p)
  }

}
