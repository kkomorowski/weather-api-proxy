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

class Endpoints(weatherService: WeatherService):

  // GET /weather/{latitude}/{longitude}
  private val weather: PublicEndpoint[(Latitude, Longitude), Unit, Conditions, Any] = endpoint.get
    .in(
      "weather"
        / path[Latitude]("latitude")
        .description("Latitude in decimal degrees. Valid range: -90.0 to 90.0.")
        .validate(Validator.min(-90.0).and(Validator.max(90.0)))
        / path[Longitude]("longitude")
        .description("Longitude in decimal degrees. Valid range: -180.0 to 180.0.")
        .validate(Validator.min(-180.0).and(Validator.max(180.0)))
    )
    .summary("Get current weather by coordinates")
    .description("Returns the current weather conditions for the supplied latitude and longitude.")
    .out(jsonBody[Conditions].description("Current weather conditions (temperature in Celsius)."))

  val weatherServerEndpoint: ZServerEndpoint[Any, Any] =
    weather.serverLogicSuccess:
      case (latitude, longitude) => weatherService.currentWeather(Coordinates(latitude, longitude))

  private val apiEndpoints: List[ZServerEndpoint[Any, Any]] = List(weatherServerEndpoint)

  private val docEndpoints: List[ZServerEndpoint[Any, Any]] = SwaggerInterpreter()
    .fromServerEndpoints[Task](apiEndpoints, "weather-api", "1.0.0")

  val all: List[ZServerEndpoint[Any, Any]] = apiEndpoints ++ docEndpoints

object Weather:
  type Latitude = Double
  type Longitude = Double
  case class Coordinates(latitude: Latitude, longitude: Longitude)
  case class Conditions(temperature: Double)

trait WeatherService:
  def currentWeather(coordinates: Coordinates): Task[Conditions]

object TestWeatherService extends WeatherService:
  def currentWeather(coordinates: Coordinates): Task[Conditions] =
    ZIO.succeed(Conditions(Math.round(coordinates.latitude * 10 + coordinates.longitude * 10) % 400 / 10.0 - 20))
