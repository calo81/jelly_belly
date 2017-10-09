name := "jelly_belly"

version := "1.0"

scalaVersion  := "2.11.11"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.11" % "2.5.6",
  "com.typesafe.akka" % "akka-http_2.11" % "10.0.10",
  "com.typesafe.akka" % "akka-persistence_2.11" % "2.5.6"

)

