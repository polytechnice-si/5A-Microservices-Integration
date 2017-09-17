package computerdatabase

import java.util.UUID

import scala.language.postfixOps

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._


class RegistrySimulation extends Simulation {

  val httpConf =
    http
      .baseURL("http://localhost:9080/tcs-service-document/")
      .acceptHeader("application/json")
      .header("Content-Type", "application/json")

  val stressSample =
    scenario("Registering Citizens")
        .repeat(10)
        {
          exec(session =>
            session.set("ssn", UUID.randomUUID().toString)
          )
            .exec(
              http("registering a citizen")
                .post("registry")
                .body(StringBody(session => buildRegister(session)))
                .check(status.is(200))
            )
            .pause(1 seconds)
            .exec(
              http("retrieving a citizen")
                .post("registry")
                .body(StringBody(session => buildRetrieve(session)))
                .check(status.is(200))
            )
        }

  def buildRegister(session: Session): String = {
    val ssn = session("ssn").as[String]
    raw"""{
      "event": "REGISTER",
      "citizen": {
        "last_name": "Doe",
        "first_name": "John",
        "ssn": "$ssn",
        "zip_code": "06543",
        "address": "nowhere, middle of",
        "birth_year": "1970"
      }
    }""""
  }


  def buildRetrieve(session: Session): String = {
    val ssn = session("ssn").as[String]
    raw"""{
      "event": "RETRIEVE",
      "ssn": "$ssn"
    }""""
  }

  setUp(stressSample.inject(rampUsers(20) over (10 seconds)).protocols(httpConf))
}