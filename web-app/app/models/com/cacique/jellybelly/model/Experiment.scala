package com.cacique.jellybelly.model

import akka.actor.{ActorLogging, Props}
import akka.persistence.PersistentActor
import play.api.libs.json.{Format, Json}

object ExperimentState {
  implicit val experimetnStateFormat: Format[ExperimentState] = Json.format[ExperimentState]
}

sealed case class ExperimentState(experimentId: String, val name: String, var variants: Seq[Variant], var participants: Seq[Participant] = Seq()) {
  def addParticipant(participant: Participant): Unit = {
    participants = participants :+ participant
  }
}

class Experiment(
                  val persistenceId: String,
                  name: String,
                  variants: Seq[Variant]
                ) extends PersistentActor with ActorLogging {

  private val state = new ExperimentState(persistenceId, name, variants)

  val updateState: Event => Unit = {
    case Participated(value) =>
      state.addParticipant(value)
      context.parent ! ExperimentUpdated(stateCopy)
  }

  def stateCopy = state.copy()

  override def receiveRecover = {
    case event: Event =>
      updateState(event)
  }

  override def receiveCommand = {
    case Participate(_, participant) =>
      persist(Participated(participant))(updateState)
    case GetExperiment(_, _) =>
      sender() ! stateCopy
      context.parent ! ExperimentUpdated(stateCopy)
    case PublishState =>
      context.parent ! ExperimentUpdated(stateCopy)
  }

}
