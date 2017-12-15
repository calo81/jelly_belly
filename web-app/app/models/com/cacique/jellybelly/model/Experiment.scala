package com.cacique.jellybelly.model

import akka.actor.{ActorLogging, Props}
import akka.persistence.PersistentActor
import play.api.libs.json.{Format, Json}

import scala.util.Random

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

  private var state: ExperimentState = _

  private var variantSpectrum: Seq[Variant] = _

  setState


  val updateState: Event => Unit = {
    case Participated(value) =>
      state.addParticipant(value)
      context.parent ! ExperimentUpdated(stateCopy)
  }

  private def stateCopy = state.copy()

  private def assignVariant(participant: Participant) = {
    val variantIndex = Random.nextInt(100)
    participant.copy(variant = Some(variantSpectrum(variantIndex)))
  }

  private def setState = {
    val variantsWithPercentage = variants.filter(_.percentage != -1)
    val consumedPercentage =
      variantsWithPercentage
        .foldLeft(0)(_ + _.percentage)
    val leftPercentage = 100 - consumedPercentage
    val variantsWithNoPercentage = variants.filter(_.percentage == -1)
    val newVariants = variantsWithNoPercentage.map(_.copy(percentage = leftPercentage / variantsWithNoPercentage.size))
    state = new ExperimentState(persistenceId, name, variantsWithPercentage ++ newVariants)
    variantSpectrum = state.variants.flatMap { v => (1 to v.percentage).map(_ => v) }
  }

  override def receiveRecover = {
    case event: Event =>
      updateState(event)
  }

  override def receiveCommand = {
    case Participate(_, participant) =>
      val participantWithAssignedVariant = assignVariant(participant)
      persist(Participated(participantWithAssignedVariant))(updateState)
    case GetExperiment(_, _) =>
      sender() ! stateCopy
      context.parent ! ExperimentUpdated(stateCopy)
    case PublishState =>
      context.parent ! ExperimentUpdated(stateCopy)
  }

}
