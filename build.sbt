name := """github-heroes"""
organization := "io.iz"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  guice,
  ws,
  "com.typesafe.play" %% "play-json" % "2.6.3",
  "com.amazonaws" % "aws-java-sdk" % "1.11.209",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "com.github.tomakehurst" % "wiremock-standalone" % "2.8.0" % Test
)