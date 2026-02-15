package dev.hiquality.weather.model

object Exceptions:
  sealed trait WeatherServiceException extends Throwable
  case class InvalidCoordinatesException(message: String) extends WeatherServiceException
  case class GenericWeatherServiceException(message: String) extends WeatherServiceException
  case class DownstreamServiceException(message: String) extends WeatherServiceException
