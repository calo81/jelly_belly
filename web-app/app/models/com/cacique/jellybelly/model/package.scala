package com.cacique.jellybelly

import akka.actor.ActorRef
import akka.stream.scaladsl.{SourceQueue, SourceQueueWithComplete}

package object model {

  sealed trait Command

  sealed trait Event

  case class Participate(experimentId: String, participant: Participant) extends Command

  case class GetExperiment(id: String, variants: Seq[Variant]) extends Command

  case class Subscribe(actor: ActorRef) extends Command

  case class Unsubscribe(actor: ActorRef) extends Command

  object PublishState extends Command

  case class Participated(participant: Participant) extends Event

  case class ExperimentCreated(experiment: ActorRef) extends Event

  case class ExperimentUpdated(experimentState: ExperimentState) extends Event

}
