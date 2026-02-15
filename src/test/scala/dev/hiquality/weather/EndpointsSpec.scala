package dev.hiquality.weather

import dev.hiquality.weather.Exceptions.DownstreamServiceException
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import sttp.client4.basicRequest
import sttp.client4.UriContext
import sttp.client4.circe.asJson
import dev.hiquality.weather.Weather.*
import org.scalatest.EitherValues
import io.circe.generic.auto.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.BackendStub
import sttp.model.StatusCode.*
import sttp.tapir.server.stub4.TapirStubInterpreter
import zio.*

class EndpointsSpec extends AnyFunSuite with Matchers with EitherValues:

  private val endpoints = new Endpoints(TestWeatherService)

  private val backend = TapirStubInterpreter(BackendStub[Task](new RIOMonadAsyncError[Any]))
    .whenServerEndpointRunLogic(endpoints.weatherServerEndpoint)
    .backend()

  private def unsafeRun[A](program: Task[A]) =
    Unsafe.unsafe:
      implicit unsafe =>
        Runtime.default.unsafe.run(program).getOrThrowFiberFailure()

  test("Weather endpoint returns actual conditions"):
    val response = unsafeRun:
      basicRequest
        .get(uri"http://localhost:8080/weather/27.6895/140.6917")
        .response(asJson[Conditions])
        .send(backend)
    response.code shouldBe Ok
    response.body.value shouldBe Conditions(-11.6)

  test("Weather endpoint with out-of-bounds coordinates returns 400"):
    val response = unsafeRun:
      basicRequest
        .get(uri"http://localhost:8080/weather/100.0/200.0")
        .response(asJson[Conditions])
        .send(backend)
    response.code shouldBe BadRequest

  test("Weather endpoint with invalid coordinates returns 400"):
    val response = unsafeRun:
      basicRequest
        .get(uri"http://localhost:8080/weather/invalid/coordinates")
        .response(asJson[Conditions])
        .send(backend)
    response.code shouldBe BadRequest

  test("Weather endpoint without coordinates returns 404"):
    val response = unsafeRun:
      basicRequest
        .get(uri"http://localhost:8080/weather/")
        .response(asJson[Conditions])
        .send(backend)
    response.code shouldBe NotFound

  test("Weather endpoint simulating downstream failure returns 502"):
    val response = unsafeRun:
      basicRequest
        .get(uri"http://localhost:8080/weather/50.0/0.0")
        .response(asJson[Conditions])
        .send(backend)
    response.code shouldBe BadGateway