name := "actors"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies ++= List(
  "com.typesafe.akka" %% "akka-actor" % "2.6.19",
  "com.softwaremill.sttp.client3" %% "core" % "3.5.1",
  "io.spray" %%  "spray-json" % "1.3.6",
  "com.xebialabs.restito" % "restito" % "0.9.4" % Test,
  "org.junit.jupiter" % "junit-jupiter-api" % "5.8.2" % Test,
  "org.junit.jupiter" % "junit-jupiter-engine" % "5.8.2" % Test,
  "org.scalactic" %% "scalactic" % "3.2.11",
  "org.scalatest" %% "scalatest" % "3.2.11" % "test"
)
