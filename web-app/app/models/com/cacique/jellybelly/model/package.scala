package com.cacique.jellybelly

import akka.actor.ActorRef

package object model {

  sealed trait Command

  sealed trait Event

  case class Participate(participant: Participant) extends Command

  case class GetExperiment(id: String) extends Command

  object GetExperiments extends Command

  case class Participated(participant: Participant) extends Event

  case class ExperimentCreated(experiment: ActorRef) extends Event

}
