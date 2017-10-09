package com.cacique.jellybelly.model

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

sealed class ExperimentState {
  private var participants: Seq[String] = Seq()

  def addParticipant(participant: String): Unit = {
    participants = participants :+ participant
  }
}

class Experiment(val persistenceId: String) extends PersistentActor with ActorLogging{

  var state = new ExperimentState

  val updateState: Event => Unit = {
    case Participated(value) => state.addParticipant(value)
  }

  override def receiveRecover = {
    case event: Event =>
      updateState(event)
  }

  override def receiveCommand = {
    case Participate(participantId) => persist(Participated(participantId))(updateState)
  }


}
