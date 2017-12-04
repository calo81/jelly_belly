name := """jelly-belly"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.jcenterRepo

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.6"
libraryDependencies += "com.typesafe.akka" %% "akka-persistence" % "2.5.6"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "com.h2database" % "h2" % "1.4.196"
libraryDependencies += "com.github.dnvriend" %% "akka-persistence-inmemory" % "2.5.1.1"
libraryDependencies += "com.typesafe.play" %% "play-iteratees" % "2.6.1"
libraryDependencies += "com.typesafe.play" %% "play-iteratees-reactive-streams" % "2.6.1"
libraryDependencies += "org.webjars" % "bootstrap" % "3.1.1-2"
libraryDependencies += "org.webjars" %% "webjars-play" % "2.6.1"
libraryDependencies += "org.webjars.npm" % "react-dom" % "15.6.1"
libraryDependencies += "org.webjars.npm" % "react" % "15.6.1"
libraryDependencies += "org.webjars" % "jquery" % "3.2.1"



