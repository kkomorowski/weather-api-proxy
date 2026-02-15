package dev.hiquality.weather.service

import dev.hiquality.weather.model.Exceptions.InvalidCoordinatesException
import dev.hiquality.weather.model.Weather.{Conditions, Coordinates}
import zio.{Task, ZIO}

trait WeatherService:
  def currentWeather(coordinates: Coordinates): Task[Conditions]
  def validateCoordinates(coordinates: Coordinates): Task[Unit] =
    if coordinates.latitude < -90.0 || coordinates.latitude > 90.0 ||
      coordinates.longitude < -180.0 || coordinates.longitude > 180.0
    then
      ZIO.fail(InvalidCoordinatesException(s"Invalid coordinates: ${coordinates.latitude}, ${coordinates.longitude}"))
    else ZIO.unit
