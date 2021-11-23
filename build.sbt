import sbt.Keys.publishArtifact

name := "prometheus-client-scala"
organization := "org.dmonix"
version := "1.1.0"
scalaVersion := "2.13.7"

scalaVersion := "2.13.7"
crossScalaVersions := Seq("2.11.12", "2.12.15", "2.13.7")
fork := true //needed to get the java-options set properly during sbt run

libraryDependencies ++= Seq(
    `prometheus-simpleclient`,
    `specs2-core` % Test,
    `specs2-mock` % Test,
    `specs2-matcher-extra` % Test,
    `prometheus-simpleclient-hotspot` % Test,
    `prometheus-simpleclient-httpserver` % Test,
  )