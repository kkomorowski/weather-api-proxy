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
import dev.hiquality.weather.Exceptions.{DownstreamServiceException, GenericWeatherServiceException, InvalidCoordinatesException, WeatherServiceException}
import sttp.model.StatusCode

class Endpoints(weatherService: WeatherService):

  // GET /weather/{latitude}/{longitude}
  private val weather: PublicEndpoint[(Latitude, Longitude), WeatherServiceException, Conditions, Any] = endpoint.get
    .in(
      "weather"
        / path[Latitude]("latitude")
        .description("Latitude in decimal degrees. Valid range: -90.0 to 90.0.")
        .example(27.6895)
        / path[Longitude]("longitude")
        .description("Longitude in decimal degrees. Valid range: -180.0 to 180.0.")
        .example(140.6917)
    )
    .summary("Get current weather conditions by coordinates")
    .description("Returns the current weather conditions for the supplied latitude and longitude.")
    .out(
      jsonBody[Conditions]
        .description("Current weather conditions (temperature in Celsius).")
        .example(Conditions(-11.6))
    )
    .errorOut(oneOf[WeatherServiceException](
      oneOfVariant[InvalidCoordinatesException](
        statusCode(StatusCode.BadRequest)
          .and(jsonBody[InvalidCoordinatesException]
            .description("The provided coordinates are invalid. Latitude must be between -90 and 90, and longitude must be between -180 and 180.")
            .example(InvalidCoordinatesException("Invalid coordinates: 100.0, 200.0"))
          )
      ),
      oneOfVariant[DownstreamServiceException](
        statusCode(StatusCode.BadGateway)
          .and(jsonBody[DownstreamServiceException]
            .description("The weather service is currently unavailable due to an issue with a downstream service. Please try again later.")
            .example(DownstreamServiceException("Simulated downstream service failure"))
          )
      ),
      oneOfDefaultVariant(
        statusCode(StatusCode.InternalServerError)
          .and(jsonBody[GenericWeatherServiceException]
            .description("An unexpected error occurred while processing the request. Please try again later.")
            .example(GenericWeatherServiceException("An unexpected error occurred: NullPointerException"))
          )
      )
    ))

  val weatherServerEndpoint: ZServerEndpoint[Any, Any] =
    weather.serverLogic:
      case (latitude, longitude) =>
        val result = for
          _          <- weatherService.validateCoordinates(Coordinates(latitude, longitude))
          conditions <- weatherService.currentWeather(Coordinates(latitude, longitude))
        yield Right(conditions)
        result.catchAll:
          case e: InvalidCoordinatesException => ZIO.succeed(Left(e))
          case e: DownstreamServiceException => ZIO.succeed(Left(e))
          case e: Throwable =>
            ZIO.succeed(Left(GenericWeatherServiceException(s"An unexpected error occurred: ${e.getMessage}")))

  private val apiEndpoints: List[ZServerEndpoint[Any, Any]] = List(weatherServerEndpoint)

  private val docEndpoints: List[ZServerEndpoint[Any, Any]] = SwaggerInterpreter()
    .fromServerEndpoints[Task](apiEndpoints, "weather-api", "1.0.0")

  val all: List[ZServerEndpoint[Any, Any]] = apiEndpoints ++ docEndpoints

object Weather:
  type Latitude = Double
  type Longitude = Double
  case class Coordinates(latitude: Latitude, longitude: Longitude)
  case class Conditions(temperature: Double)

object Exceptions:
  sealed trait WeatherServiceException extends Throwable
  case class InvalidCoordinatesException(message: String) extends WeatherServiceException
  case class GenericWeatherServiceException(message: String) extends WeatherServiceException
  case class DownstreamServiceException(message: String) extends WeatherServiceException

trait WeatherService:
  def currentWeather(coordinates: Coordinates): Task[Conditions]
  def validateCoordinates(coordinates: Coordinates): Task[Unit] =
    if coordinates.latitude < -90.0 || coordinates.latitude > 90.0 ||
       coordinates.longitude < -180.0 || coordinates.longitude > 180.0
    then ZIO.fail(InvalidCoordinatesException(s"Invalid coordinates: ${coordinates.latitude}, ${coordinates.longitude}"))
    else ZIO.unit

object TestWeatherService extends WeatherService:
  def currentWeather(coordinates: Coordinates): Task[Conditions] =
    coordinates match
      case Coordinates(lat, lon) if lat == 50 && lon == 0 =>
        ZIO.fail(DownstreamServiceException("Simulated downstream service failure"))
      case _ =>
        ZIO.succeed(Conditions(Math.round(coordinates.latitude * 10 + coordinates.longitude * 10) % 400 / 10.0 - 20))