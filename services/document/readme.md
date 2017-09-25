# Creating a Document service

  * Author: Sébastien Mosser [(email)](mosser@i3s.unice.fr)
  * Version: 2017.09
  * Reviewer: [Mireille Blay-Fornarino](blay@i3s.unice.fr)

## Creating the Maven project

We implement this service using the Java language, and use Maven to support the project description. The descriptor is located in the `pom.xml` file, and inherits its content from the global one described in the `service` directory (mainly the application server configuration).  The file system hierarchy is the following:

```
Azrael:document mosser$ tree .
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

The `Registry` service is use to store citizens inside a global registry (_Folkeregisteret_). It follows a document approach, and handle the following events:

  - `REGISTER`: registers a citizen;
  - `RETRIEVE`: get a citizen based on his/her social security number.
  - `DELETE`: deletes a citizen;
  - `LIST`: lists citizens with matching a given regular expression;
  - `DUMP`: lists all citizens;
  - `PURGE`: delete the contents of the registry (use with caution);

### Implementing the service

The service produces `application/json` data, and cosumes it as well. It defines a single route named `registry`, with a single method `process`. The request is posted to the `registry` endpoint, accepting a body made of a JSON document:

```json
{ "event": "REGISTER", ...}
```

```java
@Path("/registry")
@Produces(MediaType.APPLICATION_JSON)
public class Registry {

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response process(String input) {
    JSONObject obj = new JSONObject(input);
    try {
      switch (EVENT.valueOf(obj.getString("event"))) {
          // Dispatch code goes here
      }
    } catch(Exception e) {
      JSONObject error = new JSONObject().put("error", e.toString());
      return Response.status(400).entity(error.toString(INDENT_FACTOR)).build();
    }
    return null;
  }
}
```

The service is implemented in the [Registry](https://github.com/polytechnice-si/5A-Microservices-Integration/blob/master/services/document/src/main/java/registry/Registry.java) class.

### Business code

The Business code is implemented in the [Handler](https://github.com/polytechnice-si/5A-Microservices-Integration/blob/master/services/document/src/main/java/registry/Handler.java) class. It relies on a MongoDB database. For example, to register a citizen inside the registry

```java
static JSONObject register(JSONObject input) {
  MongoCollection citizens = getCitizens();
  Citizen data = new Citizen(input.getJSONObject("citizen"));
  citizens.insert(data);
  return new JSONObject().put("inserted", true).put("citizen",data.toJson());
}
```

The dispatch code is straightforward:

```java
case REGISTER:
  return Response.ok().entity(Handler.register(obj).toString()).build();
```

## Starting the service

  * Compiling: `mvn clean package` will create the file `target/tcs-service-document.war`
  * Running: `mvn tomee:run` will deploy the created `war` inside a TomEE+ server, available on `localhost:8080`
  * The service is available at [http://localhost:8080/tcs-service-document/registry](http://localhost:8080/tcs-service-document/registry)
