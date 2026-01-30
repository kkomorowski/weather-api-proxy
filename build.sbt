val tapirVersion = "1.13.6"

lazy val rootProject = (project in file(".")).settings(
  Seq(
    name := "weather-api",
    version := "0.1.0-SNAPSHOT",
    organization := "dev.hiquality.weather",
    scalaVersion := "3.7.4",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server-zio" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
      "org.http4s" %% "http4s-ember-server" % "0.23.33",
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.27",
      "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub4-server" % tapirVersion % Test,
      "com.softwaremill.sttp.client4" %% "circe" % "4.0.15" % Test,
      "com.softwaremill.sttp.client4" %% "zio" % "4.0.15" % Test,
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    ),
    testFrameworks := Seq(new TestFramework("org.scalatest.tools.Framework"))
  )
)
