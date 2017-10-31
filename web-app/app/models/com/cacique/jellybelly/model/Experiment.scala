package com.cacique.jellybelly.model

import akka.actor.{ActorLogging, Props}
import akka.persistence.PersistentActor

sealed class ExperimentState(experimentId: String, val name: String, var variants: Seq[Variant]) {
  private var participants: Seq[Participant] = Seq()

  def addParticipant(participant: Participant): Unit = {
    participants = participants :+ participant
  }
}

class Experiment(
                  val persistenceId: String,
                  name: String,
                  variants: Seq[Variant]
                ) extends PersistentActor with ActorLogging{

  var state = new ExperimentState(persistenceId, name, variants)

  val updateState: Event => Unit = {
    case Participated(value) => state.addParticipant(value)
  }

  override def receiveRecover = {
    case event: Event =>
      updateState(event)
  }

  override def receiveCommand = {
    case Participate(participantId) => persist(Participated(participantId))(updateState)
    case GetExperiment(_) => sender() ! state
  }

}
