import sbt._


object Dependencies extends AutoPlugin {

  object autoImport {
    val `prometheus-simpleclient` = "io.prometheus" % "simpleclient" % "0.11.0"
    val `prometheus-simpleclient-hotspot` = "io.prometheus" % "simpleclient_hotspot" % `prometheus-simpleclient`.revision
    val `prometheus-simpleclient-httpserver` = "io.prometheus" % "simpleclient_httpserver" % `prometheus-simpleclient`.revision

    val `slf4j-simple` = "org.slf4j" % "slf4j-simple" % "1.7.30"

    val `specs2-core`          = "org.specs2" %% "specs2-core"          % "4.10.6"
    val `specs2-mock`          = "org.specs2" %% "specs2-mock"          % `specs2-core`.revision
    val `specs2-matcher-extra` = "org.specs2" %% "specs2-matcher-extra" % `specs2-core`.revision
  }

}