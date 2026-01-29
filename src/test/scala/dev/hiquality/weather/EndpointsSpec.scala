package dev.hiquality.weather

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import sttp.client4.basicRequest
import sttp.client4.UriContext
import sttp.client4.circe.asJson
import dev.hiquality.weather.Weather.*
import org.scalatest.EitherValues
import io.circe.generic.auto.*
import sttp.client4.impl.zio.RIOMonadAsyncError
import sttp.client4.testing.BackendStub
import sttp.model.StatusCode.Ok
import sttp.tapir.server.stub4.TapirStubInterpreter
import zio.*

class EndpointsSpec extends AnyFunSuite with Matchers with EitherValues:

  private val backend = TapirStubInterpreter(BackendStub[Task](new RIOMonadAsyncError[Any]))
    .whenServerEndpointRunLogic(Endpoints.weatherServerEndpoint)
    .backend()

  private def unsafeRun[A](program: Task[A]) =
    Unsafe.unsafe:
      implicit unsafe =>
        Runtime.default.unsafe.run(program).getOrThrowFiberFailure()

  test("Weather endpoint returns actual conditions"):
    val response = unsafeRun:
      basicRequest
        .get(uri"http://localhost:8080/weather")
        .response(asJson[Conditions])
        .send(backend)
    response.code shouldBe Ok
    response.body.value shouldBe Conditions(-13)