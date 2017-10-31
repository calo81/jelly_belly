package com.cacique.jellybelly.model

case class Variant(name: String, percentage: Int = -1) {
  assert(percentage > -1 && percentage < 100)
}
