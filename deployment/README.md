# Composing containers

  * Author: Sébastien Mosser [(email)](mosser@i3s.unice.fr)
  * Version: 2017.09
  * Reviewer: [Mireille Blay-Fornarino](blay@i3s.unice.fr)

  
  
## Creating a Docker Compose deployment

_"Interesting applications rarely live in isolation"_. With respect to that point, Docker supports the composition of containers into deployment units called _compositions_. A composition is modelled by a YAML file (be default `docker-compose.yml`), describing how the images must be configured for this very deployment.

The following descriptor describes a composition deploying two containers (named `tcs-computation` and `tcs-generator`), running two images (respectively `petitroll/tcs-rpc` and `petitroll/tcs-rest`).

```yaml
version: '3'
services:

  tax-computation:                
    container_name: tcs-computation
    image: petitroll/tcs-rpc

  id-generator:                   
    container_name: tcs-generator
    image: petitroll/tcs-rest
```

To start the composition as a daemon:

```
azrael:deployment mosser$ docker-compose up -d
```

To stop the composition (it will stop and then remove the containers):

```
azrael:deployment mosser$ docker-compose down

```

## Common operations when composing containers

### Port redirection

The previously defined composition does not deploy properly: the two containers expose a service on the very same port (`8080`), leading to a port conflict. To avoid this, one needs to redirect ports inside the composition. For example, the `tcs-computation` container will be bound to port `9090`, thus a request to `localhost:9090` will be transparently redirected to `tcs-computation:8080` by the docker engine. 

```yaml
services:

  tax-computation:                
    container_name: tcs-computation
    image: petitroll/tcs-rpc
    ports:
      - "9090:8080"					# localhost:9090 --> tcs-computation:8080

  id-generator:                   
    container_name: tcs-generator
    image: petitroll/tcs-rest
    ports:
      - "9070:8080"					# localhost:9070 --> tcs-generator:8080
```

### File system interaction

Containers are isolated from the host machine (to a given extent). But sometimes, the container must access to the physical filesystem, for example to store data in a resilient way.  

To do so, we rely on Docker _volumes_. The container relies on a given volume, mapping a local directory to a directory that exists inside the container. For example, in the following snippet, the MongoDB database is deployed with a binding that links `./mongo_data` on the host to the `/data/db` directory inside the container.

```yaml
database:
    container_name: tcs-database
    image: mongo:3.0
    volumes:
      - "./mongo_data:/data/db"
    ports:
      - "27017:27017"
```

### Dependencies between containers

It is common for a container to rely on another one. For example, the citizen registry relies on a MongoDB database, and it is necessary for the database to be up before the registry can connect to it. Docker supports dependencies between containers, to start a container after the start of another one. In the following example, the `tcs-citizen` will start after the `tcs-database` one.

```yaml
citizen-registry:              
    container_name: tcs-citizens
    image: petitroll/tcs-doc
    depends_on:
      - database
    ports:
      - "9080:8080"
```

__Remarks__: This approach does not implies that the database will be _up and running_ when the registry is started. The container can be started, but the MongoDB might take some additional seconds to be ready to accept connection. To address this issue, one must write a shell script dedicated to checking the status of the database (see [official docker documentation](https://docs.docker.com/compose/startup-order/)).

### Accessing a container from another one

Containers are isolated from each others. Accessing `localhost` refers to the local system deployed inside the container. Inside a composition, Docker deploys each container with a network name equals to the container name. Thus, for a container to refer to another container `my-awesome-container`, one needs to use this logical name.

For example, the `registry` service refers to the MongoDB instance hosted in the `database` container using this logical name when connecting to the database.

```
private static MongoCollection getCitizens() {
	MongoClient client = new MongoClient("tcs-database", 27017);
	return new Jongo(client.getDB(Network.DATABASE)).getCollection(Network.COLLECTION);
}
```

__Remarks__: referring to hostnames in an hardcoded way is obviously a bad practice. One should deploy a script that configure it at deployment time. To do so, look at the DockerFile associated to the `Registry` service. It replaces the `ENTRYPOINT` by an homemade script, `start_in_docker.sh`. 

```
ENTRYPOINT ["./start.sh"]
```

When starting, the script reads two environment variables `db_host` and `db_port`, and update a config file (`network.properties`) in the service war file to use the right configuration. This configuration must be done when the service starts, and cannot be done at build-time (which would be equivalent to hard-code the hostname inside the image).

```bash
mkdir -p ./WEB-INF/classes/
echo "databaseHostName=$db_host" > ./WEB-INF/classes/network.properties
echo "databasePort=$db_port" >> ./WEB-INF/classes/network.properties
jar uvf ./webapps/tcs-service-document.war ./WEB-INF/classes/network.properties
```


In the compose file, the environment variables are set using `environment` entries:

```yaml
citizen-registry:             
    container_name: tcs-citizens
    image: petitroll/tcs-doc
    environment:
      - db_host=tcs-database
      - db_port=27017
    depends_on:
      - database
    ports:
      - "9080:8080"
```

