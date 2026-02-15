addCommandAlias("format", "all scalafmtSbt scalafmt")
addCommandAlias("formatCheck", "all scalafmtSbtCheck scalafmtCheck")

val tapirVersion = "1.13.8"

lazy val rootProject = (project in file(".")).settings(
  Seq(
    name                := "weather-api",
    version             := "0.1.0-SNAPSHOT",
    organization        := "dev.hiquality.weather",
    scalaVersion        := "3.7.4",
    Compile / mainClass := Some("dev.hiquality.weather.Main"),

    // Build a fat JAR with proper merge strategies
    assembly / assemblyJarName       := s"${name.value}-${version.value}.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "maven", "org.webjars", "swagger-ui", "pom.properties") =>
        MergeStrategy.singleOrError
      case PathList("META-INF", "resources", "webjars", "swagger-ui", _*) =>
        MergeStrategy.singleOrError
      case PathList("META-INF", "services", _*) => MergeStrategy.concat
      case PathList("META-INF", _*)             => MergeStrategy.discard
      case "module-info.class"                  => MergeStrategy.first
      case x                                    =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    },

    // Dependencies
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server-zio" % tapirVersion,
      "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server"     % tapirVersion,
      "org.http4s"                    %% "http4s-ember-server"     % "0.23.33",
      "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui-bundle" % tapirVersion,
      "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"        % tapirVersion,
      "ch.qos.logback"                 % "logback-classic"         % "1.5.31",
      "com.softwaremill.sttp.tapir"   %% "tapir-sttp-stub4-server" % tapirVersion % Test,
      "com.softwaremill.sttp.client4" %% "circe"                   % "4.0.15"     % Test,
      "com.softwaremill.sttp.client4" %% "zio"                     % "4.0.15"     % Test,
      "org.scalatest"                 %% "scalatest"               % "3.2.19"     % Test
    ),
    testFrameworks := Seq(new TestFramework("org.scalatest.tools.Framework"))
  )
)

ThisBuild / scalafmtSbt := true
