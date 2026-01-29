package dev.hiquality.weather

import sttp.tapir.*
import io.circe.generic.auto.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import zio.Task
import zio.ZIO
import Weather.*

object Endpoints:

  private val weather: PublicEndpoint[Unit, Unit, Conditions, Any] = endpoint.get
    .in("weather")
    .out(jsonBody[Conditions])

  val weatherServerEndpoint: ZServerEndpoint[Any, Any] = weather.serverLogicSuccess(_ => ZIO.succeed(Weather.current))

  val apiEndpoints: List[ZServerEndpoint[Any, Any]] = List(weatherServerEndpoint)

  private val docEndpoints: List[ZServerEndpoint[Any, Any]] = SwaggerInterpreter()
    .fromServerEndpoints[Task](apiEndpoints, "weather-api", "1.0.0")

  val all: List[ZServerEndpoint[Any, Any]] = apiEndpoints ++ docEndpoints

object Weather:
  case class Conditions(temperature: Int)
  val current = Conditions(-13)
