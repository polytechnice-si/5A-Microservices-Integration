# Wrapping Services in containers

  * Author: Sébastien Mosser [(email)](mosser@i3s.unice.fr)
  * Version: 2017.09
  * Reviewer: [Mireille Blay-Fornarino](blay@i3s.unice.fr)


__Warning__: Docker requires access to a virtualisation system. It is not compatible with Windows Family.

## Creating a Dockerfile

A docker file extends a previous docker image, and contains setup instructions to configure the service. When using Tomee, one can reuse off-the-shelf images published by the TomEE developers: [https://hub.docker.com/_/tomee/](https://hub.docker.com/_/tomee/)

We are developing in Java 8 and using the TomEE+ server, so our images will extend the `8-jre-7.0.3-plus` image. Defining the image is straightforward:

  1. Extending the official TomEE+ image (`FROM`)
  2. Going into the `/usr/local/tomee` server (`WORKDIR`)
  3. Copying the `war` file containing the service into the image (`CP`)
  4. Declaring the port used by the server (`EXPOSE`)

```docker
FROM tomee:8-jre-7.0.3-plus
MAINTAINER Sébastien Mosser (mosser@i3s.unice.fr)
WORKDIR /usr/local/tomee/
COPY ./target/my-awesome-service.war ./webapps/.
EXPOSE 8080
```

## Building the image

To build the image and make it available on the local machine, simply use the `docker build` command. We use the `-t` option to tag the built image with a reusable name:

```
mosser@azrael service$ docker build -t petitroll/my-service
```

## Working with the image

### Starting a container

To start a container that will contain the build image, the `docker run` command is the one to use. We use `-d` option to start the container as a daemon (non interactive), and the `--name` option to give a name to it.

```
mosser@azrael service$ docker run -d --name my_container petitroll/my-service 
```

### Stopping the container

```
mosser@azrael service$ docker stop my_container 
```

### Removing the container

```
mosser@azrael service$ docker rm my_container 
```