# Creating a Resource service

  * Author: Sébastien Mosser [(email)](mosser@i3s.unice.fr)
  * Version: 2017.09
  * Reviewer: [Mireille Blay-Fornarino](blay@i3s.unice.fr)

## Creating the Maven project

We implement this service using the Java language, and use Maven to support the project description. The descriptor is located in the `pom.xml` file, and inherits its content from the global one described in the `service` directory (mainly the application server configuration).  The file system hierarchy is the following:

```
azrael:resource mosser$ tree .
.
├── README.md
├── pom.xml
└── src
    └── main
        ├── java
        │   └── # service code goes here
        └── webapp
            └── WEB-INF
                └── web.xml
```

## Developing the service

The `Generator` service is use to make tax computation anonymous. It creates counters, and generate unique identifiers based on these counters. The system is transient, _i.e._, there is no database associated to the system. 

### Implementing the service

The service produces `application/json` data. It defines several routes under the `generator` prefix, to create generators, get all available generators, generate a new identifier or delete a generator.

```java
@Path("/generators")
@Produces(MediaType.APPLICATION_JSON)
public class GeneratorService {

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public Response createNewGenerator(String name) { ... }

	@GET
	public Response getAvailableGenerators() { ... }

	@Path("/{name}")
	@GET
	public Response generateIdentifier(@PathParam("name") String name) { ... }

	@Path("/{name}")
	@DELETE
	public Response deleteGenerator(@PathParam("name") String name) { ... }

}
``` 

The service is implemented in the [GeneratorService](https://github.com/polytechnice-si/5A-Microservices-Integration/blob/master/services/resource/src/main/java/gen/GeneratorService.java) class.

## Starting the service

  * Compiling: `mvn clean package` will create the file `target/tcs-service-rest.war`
  * Running: `mvn tomee:run` will deploy the created `war` inside a TomEE+ server, available on `localhost:8080`
  * The service is available at [http://localhost:8080/tcs-service-rest/generators](http://localhost:8080/tcs-service-rest/generators)

