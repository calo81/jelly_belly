package com.cacique.jellybelly

import akka.actor.ActorRef
import akka.stream.scaladsl.{SourceQueue, SourceQueueWithComplete}

package object model {

  sealed trait Command

  sealed trait Event

  case class Participate(participant: Participant) extends Command

  case class GetExperiment(id: String) extends Command

  case class GetExperiments(queue: SourceQueueWithComplete[ExperimentState]) extends Command

  case class Participated(participant: Participant) extends Event

  case class ExperimentCreated(experiment: ActorRef) extends Event

  case class ExperimentUpdated(experimentState: ExperimentState) extends Event

}
