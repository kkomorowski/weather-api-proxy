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
