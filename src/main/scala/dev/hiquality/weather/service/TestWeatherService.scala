package dev.hiquality.weather.service

import dev.hiquality.weather.model.Exceptions.DownstreamServiceException
import dev.hiquality.weather.model.Weather.{Conditions, Coordinates}
import zio.{Task, ZIO}

object TestWeatherService extends WeatherService:
  def currentWeather(coordinates: Coordinates): Task[Conditions] =
    coordinates match
      case Coordinates(lat, lon) if lat == 50 && lon == 0 =>
        ZIO.fail(DownstreamServiceException("Simulated downstream service failure"))
      case _ =>
        ZIO.succeed(Conditions(Math.round(coordinates.latitude * 10 + coordinates.longitude * 10) % 400 / 10.0 - 20))
