package dev.hiquality.weather.model

object Weather:
  type Latitude  = Double
  type Longitude = Double
  case class Coordinates(latitude: Latitude, longitude: Longitude)
  case class Conditions(temperature: Double)
