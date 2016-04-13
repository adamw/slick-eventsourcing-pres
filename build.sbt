organization  := "com.softwaremill"

name := "slick-eventsourcing-pres"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.3"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "org.flywaydb" % "flyway-core" % "4.0",
  "com.h2database" % "h2" % "1.3.176",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
  "com.softwaremill.events" %% "core" % "0.1.7-SNAPSHOT"
)
