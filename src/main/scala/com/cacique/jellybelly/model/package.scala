package com.cacique.jellybelly

package object model {

  sealed trait Command

  sealed trait Event

  case class Participate(participantId: String) extends Command

  case class Participated(participantId: String) extends Event

}
