# Stress Testing for Services

  * Author: Sébastien Mosser [(email)](mosser@i3s.unice.fr)
  * Version: 2017.09
  * Reviewer: [Mireille Blay-Fornarino](blay@i3s.unice.fr)

  
## Configuring the project

The stress test suite is implemented in a maven project. In the [pom.xml](https://github.com/polytechnice-si/5A-Microservices-Integration/blob/master/tests/stress/pom.xml) file, we:

  * Require Gatling, the stress test engine
  * Require the Scala language (Gatling relies on Scala)

We also configure the maven build phase to trigger the Scala compiler, and the Gatling plugin when required.

## Writing a Stress test

A stress test is a plan Scala program, modelling how the service must be stressed. We start by defining an `httpConf` variable that configures the target. Here, we are targeting the `Registry` service, sending and receiving `son` data.


```scala
val httpConf = http.baseURL("http://localhost:9080/tcs-service-document/")
		.acceptHeader("application/json")
		.header("Content-Type", "application/json")
```

Then, we define a scenario. In the following code, the scenario will repeat 10 times the following steps: _(i)_ putting inside the session a unique identifier used as citizen identifier, _(ii)_ sending a request to the registry to register a citizen, _(iii)_ pause for 1 second, _(iv)_ retrieve the very same citizen in the registry.

```scala
val stressSample = scenario("Registering Citizens")
		.repeat(10)
		{
			exec(session => session.set("ssn", UUID.randomUUID().toString)
			.exec(http("registering a citizen")
						.post("registry")
						.body(StringBody(session => buildRegister(session)))
						.check(status.is(200)))
			.pause(1 seconds)
			.exec(http("retrieving a citizen")
						.post("registry")
						.body(StringBody(session => buildRetrieve(session)))
						.check(status.is(200)))
		}
```  

The scenario uses two helper methods `buildRegister` and `buildRetrieve` that creates the JSON requests expected by the service. For example, the `buildRetrieve` method read the `ssn` value in the session, and create the String to be returned (Scala returns by default the last result of the last expression)

```scala
def buildRetrieve(session: Session): String = {
	val ssn = session("ssn").as[String]
	raw"""{ "event": "RETRIEVE", "ssn": "$ssn" }""""
  }
```

## Running a Stress Test

The stress test is defined in a class that extends the `Simulation` class provided by Gatling. The class calls the `setUp` function to configure how the scenario must be executed. In our example, we asked Gatling to consider 20 users executing this scenarios. The users will take 10 seconds to arrive in the simulation (ramps up phase).

```scala
class RegistrySimulation extends Simulation {
	val httpConf = ...
	val stressSample = ...

	def buildRetrieve(session: Session): String = { ... }
	def buildSession(session: Session): String = { ... }

	setUp(stressSample.inject(rampUsers(20) over (10 seconds)).protocols(httpConf))
}
```

First we compile the Scala code using the classical maven `package` goal. This can be done offline, and must not require a connexion to an up and running system.

```
azrael:stress mosser$ mvn -q package
```

Then, we trigger the Gatling engine using the `gatling:execute` goal.

```
azrael:stress mosser$ mvn -q gatling:execute

Simulation computerdatabase.RegistrySimulation started...

...

================================================================================
2017-09-27 14:29:43                                          15s elapsed
---- Requests ------------------------------------------------------------------
> Global                                                   (OK=321    KO=0     )
> registering a citizen                                    (OK=167    KO=0     )
> retrieving a citizen                                     (OK=154    KO=0     )

---- Registering Citizens ------------------------------------------------------
[#########################-------------------------------------------------] 35%
          waiting: 0      / active: 13     / done:7     
================================================================================

...

Simulation computerdatabase.RegistrySimulation completed in 20 seconds
Parsing log file(s)...
Parsing log file(s) done
Generating reports...

================================================================================
---- Global Information --------------------------------------------------------
> request count                                        400 (OK=400    KO=0     )
> min response time                                      8 (OK=8      KO=-     )
> max response time                                    229 (OK=229    KO=-     )
> mean response time                                    25 (OK=25     KO=-     )
> std deviation                                         20 (OK=20     KO=-     )
> response time 50th percentile                         20 (OK=20     KO=-     )
> response time 75th percentile                         28 (OK=28     KO=-     )
> response time 95th percentile                         56 (OK=56     KO=-     )
> response time 99th percentile                        101 (OK=101    KO=-     )
> mean requests/sec                                     20 (OK=20     KO=-     )
---- Response Time Distribution ------------------------------------------------
> t < 800 ms                                           400 (100%)
> 800 ms < t < 1200 ms                                   0 (  0%)
> t > 1200 ms                                            0 (  0%)
> failed                                                 0 (  0%)
================================================================================

Reports generated in 1s.
Please open the following file: ./src/test/results/registrysimulation-1506515368281/index.html
```

## Visualising stress test results

Gatling generates an HTML report, located in the `/src/test/results` directory. The report is interactive, you can explore it to visualise the results.

<p align="center">
	<img src="https://raw.githubusercontent.com/polytechnice-si/5A-Microservices-Integration/master/tests/stress/gatling_screenshot.png" />
</p>
