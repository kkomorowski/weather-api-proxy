package dev.hiquality.weather

import com.comcast.ip4s.{Host, Port, port}
import dev.hiquality.weather.endpoint.WeatherEndpoints
import dev.hiquality.weather.service.TestWeatherService
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import zio.interop.catz.*
import zio.stream.interop.fs2z.io.networkInstance
import zio.{Console, Scope, Task, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault:

  override def run: ZIO[Any & ZIOAppArgs & Scope, Any, Any] =

    val endpoints = new WeatherEndpoints(TestWeatherService)
    val routes    = ZHttp4sServerInterpreter().from(endpoints.all).toRoutes[Any]

    val port = sys.env
      .get("HTTP_PORT")
      .flatMap(_.toIntOption)
      .flatMap(Port.fromInt)
      .getOrElse(port"8080")

    EmberServerBuilder
      .default[Task]
      .withHost(Host.fromString("localhost").get)
      .withPort(port)
      .withHttpApp(Router("/" -> routes).orNotFound)
      .build
      .use: server =>
        for
          _ <- Console.printLine(
            s"Go to http://localhost:${server.address.getPort}/docs to open SwaggerUI. Press ENTER key to exit."
          )
          _ <- Console.readLine
        yield ()
