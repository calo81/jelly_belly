package com.cacique.jellybelly.model

import play.api.libs.json.{Format, Json}

case class Participant(id: String)

object Participant {
  implicit val participantFormat: Format[Participant] = Json.format[Participant]
}

