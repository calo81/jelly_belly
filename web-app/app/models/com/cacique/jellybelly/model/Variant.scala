package com.cacique.jellybelly.model

import play.api.libs.json.{Format, Json}

case class Variant(name: String, percentage: Int = -1) {
  assert(percentage > -1 && percentage < 100)
}

object Variant {
  implicit val variantFormat: Format[Variant] = Json.format[Variant]
}