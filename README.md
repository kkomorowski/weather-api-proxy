# Weather API Proxy

A backend for retrieving the weather data from online sources and serving a REST API

## Quick start

```shell
sbt compile # build the project
sbt test # run the tests
sbt run # run the application (Main.scala)
sbt clean assembly # create a fat jar in the target/scala-* directory

# run the JAR
java -jar target/scala-3.7.4/weather-api-0.0.1.jar

# example API call
curl http://localhost:8080/weather/37.7749/-122.4194 
```

## Swagger UI
Once the application is running, you can access the Swagger UI at: 
http://localhost:8080/docs

## API Endpoints

### GET /weather/{latitude}/{longitude}

Retrieve the current weather data for the specified latitude and longitude.
- **Parameters:**
  - `latitude` (float): Latitude in decimal degrees. Valid range: -90.0 to 90.0.
  - `longitude` (float): Longitude in decimal degrees. Valid range: -180.0 to 180.0.
- **Response:**
  - `200 OK`: Returns the weather data in JSON format.
  - `400 Bad Request`: Invalid latitude or longitude.
  - `502 Bad Gateway`: Error fetching data from the weather API.

## Example request to open-meteo.com API

This application uses the Open-Meteo API to fetch weather data.
For simplicity, it retrieves only the current temperature at 2 meters above ground level.

To get the current temperature for a specific location (latitude: 51.76, longitude: 19.44), you can use the following `curl` command:

```shell
curl -s "https://api.open-meteo.com/v1/forecast?latitude=51.76&longitude=19.44&current=temperature_2m" | jq
```

Example response:

```json
{
  "latitude": 51.764503,
  "longitude": 19.453964,
  "generationtime_ms": 0.0362396240234375,
  "utc_offset_seconds": 0,
  "timezone": "GMT",
  "timezone_abbreviation": "GMT",
  "elevation": 206.0,
  "current_units": {
    "time": "iso8601",
    "interval": "seconds",
    "temperature_2m": "°C"
  },
  "current": {
    "time": "2026-02-15T23:00",
    "interval": 900,
    "temperature_2m": -7.2
  }
}
```