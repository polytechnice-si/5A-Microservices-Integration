# Acceptance Testing of Services

  * Author: Sébastien Mosser [(email)](mosser@i3s.unice.fr)
  * Version: 2017.09
  * Reviewer: [Mireille Blay-Fornarino](blay@i3s.unice.fr)

  
## Acceptance Scenarios

An acceptance scenario is defined as a sequence of service call that achieve a given business goal. A classical way of defining an acceptance scenario is to use the _Gherkin_ language, which relies on a _Given / When / Then_ pattern. A scenario defines:

  1. An initial context (a sequence of _Given_ sentences)
  2. A triggering action (a sequence of _When_ sentences)
  3. Assumptions to validates (a sequence of _Then_ sentences).

For example, the following scenario address the `Registry` service :

```gherkin
Given an empty registry deployed on localhost:9080
	And a citizen named John added to the registry
	And a citizen named Jane added to the registry
When the DUMP message is sent
Then the answer contains 2 results
``` 

## Writing Acceptance Scenarios

We will rely on the `Cucumber` framework to address the acceptance testing challenge in this context. Cucumber maps _Gherkin_ specifications to JUnit tests using plain regular expressions.

### Declaring Acceptance Tests

We create an Unit test in the test directory, that basically does nothing but triggering Cucumber:

```java
package scenarios;

import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
public class RunCucumber { }
```

By default, Cucmber will expect scenarios as file named `.feature` and located in the `src/test/resources` directory.

### Configuring tests in Maven

As these tests are acceptance one, they will be executed against a running system. Thus, one must be able to compile the code without requiring to connect to the TCS. We move the Cucumber tests to the `integration-test` phase in Maven [pom.xml](https://github.com/polytechnice-si/5A-Microservices-Integration/blob/master/tests/acceptation/pom.xml), which will then trigger classical JUnit tests when _package_-ing and acceptance tests when _verify_-ing.

```xml
<execution>
	<id>integration-test</id>
	<goals>
		<goal>test</goal>
	</goals>
	<phase>integration-test</phase>
	<configuration>
		<excludes>
			<exclude>**/*Test.java</exclude>
		</excludes>
		<includes>
			<include>**/RunCucumber.java</include>
		</includes>
	</configuration>
</execution>
```


### Mapping Regular Expressions to JUNit tests

The different sentences are classical Java methods, annotated by `@Given`, `@When` and `@Then` metadata. Each annotation supports regular expressions, which are mapped to method parameters.

```java
@Given("^an empty registry deployed on (.*):(\\d+)$")
public void set_clean_registry(String host, int port) {
	this.host = host; this.port = port;
	JSONObject ans = call(new JSONObject().put("event", "PURGE").put("use_with", "caution"));
	assertEquals("done", ans.getString("purge"));
}
```

With this definition:

  - Writing `Given an empty registry deployed on localhost:9090`
  - Cucumber will execute `set_clean_registry("localhost", 9090)`

### Writing an Acceptance scenario

Each feature can be repetitive with respect to the context definition. Gherkin defines a `Background` entry that will be common to all the scenarios defined in a given feature:

```gherkin
Background:
	Given The TCS service deployed on localhost:9090

Scenario: Paying taxes using the "simple" method
	Given a taxpayer identified as 111-555-111
		And an income of 12000 kroner
	When the simple computation method is selected
		And the service is called
	Then the computed tax amount is 2400.0
		And the answer is associated to 111-555-111
		And the computation date is set
```

## Executing Acceptance Scenarios

First, we need to compile the code by calling the `package` goal. This phase does not require an up and running system.

```
azrael:acceptation mosser$ mvn -q package
```

The scenarios are triggered by the `verify` goal

```
azrael:acceptation mosser$ mvn -q verify

-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running scenarios.RunCucumber

11 Scenarios (11 passed)
92 Steps (92 passed)
0m2.501s

Tests run: 103, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.967 sec - in scenarios.RunCucumber

Results :

Tests run: 103, Failures: 0, Errors: 0, Skipped: 0
```
