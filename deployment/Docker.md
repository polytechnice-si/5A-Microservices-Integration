# Wrapping Services in containers

  * Author: Sébastien Mosser [(email)](mosser@i3s.unice.fr)
  * Version: 2017.09
  * Reviewer: [Mireille Blay-Fornarino](blay@i3s.unice.fr)


__Warning__: Docker requires access to a virtualisation system. It is not compatible with Windows Family.

## Describing a Docker image

### Creating a Dockerfile

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

### Building the image

To build the image and make it available on the local machine, simply use the `docker build` command. We use the `-t` option to tag the built image with a reusable name:

```
mosser@azrael service$ docker build -t petitroll/my-service
```

### Adding an healthcheck mechanism

To check the health of a container, one can add inside the image an health check status monitor, _i.e._, a small script that will terminate in an error state if something is going wring inside the image. In our case, a simple `GET` to the TomEE homepage helps to know if the application server is up and running, or not.

```docker
HEALTHCHECK --interval=5s CMD curl --fail http://localhost:8080/ || exit 1
```

## Deploying an image in a container

__One can work with an image only if the image is available. An image is available if it was built on this computer, or if it is published on a repository (_e.g._, the DockerHub).__

### Starting a container

To start a container that will contain the build image, the `docker run` command is the one to use. We use `-d` option to start the container as a daemon (non interactive), and the `--name` option to give a name to it. This operation creates a container, deploys the given image inside and then start it. 

```
mosser@azrael service$ docker run -d --name my_container petitroll/my-service 
```

The `ps` command allows one to see the different containers running on the machine.

```
azrael:tmp mosser$ docker ps
CONTAINER ID        IMAGE                COMMAND                  CREATED             STATUS              PORTS                      NAMES
219df56135b1        petitroll/tcs-doc    "catalina.sh run"        3 seconds ago       Up 4 seconds        0.0.0.0:9080->8080/tcp     tcs-citizens
31f46c3e7412        mongo:3.0            "docker-entrypoint..."   6 seconds ago       Up 5 seconds        0.0.0.0:27017->27017/tcp   tcs-database
d5f3c47c02c5        petitroll/tcs-rpc    "catalina.sh run"        6 seconds ago       Up 4 seconds        0.0.0.0:9090->8080/tcp     tcs-computation
40ba21cd9c36        petitroll/cadvisor   "/usr/bin/cadvisor..."   6 seconds ago       Up 6 seconds        0.0.0.0:8090->8080/tcp     tcs-monitoring
d5cd1852aa2a        petitroll/tcs-rest   "catalina.sh run"        6 seconds ago       Up 6 seconds        0.0.0.0:9070->8080/tcp     tcs-generator
```

When using healthcheck mechanism, docker adds extra-information inside the `STATUS` column.

```
CONTAINER ID        IMAGE                COMMAND                  CREATED             STATUS                    PORTS                      NAMES
174c8965cce8        petitroll/tcs-rpc    "catalina.sh run"        18 seconds ago      Up 16 seconds (healthy)   0.0.0.0:9090->8080/tcp     tcs-computation
968abb414f30        petitroll/tcs-rest   "catalina.sh run"        18 seconds ago      Up 17 seconds (healthy)   0.0.0.0:9070->8080/tcp     tcs-generator
c822997d460f        petitroll/tcs-doc    "./start.sh"             16 minutes ago      Up 16 seconds (healthy)   0.0.0.0:9080->8080/tcp     tcs-citizens
a204d8f920cc        mongo:3.0            "docker-entrypoint..."   18 minutes ago      Up 17 seconds             0.0.0.0:27017->27017/tcp   tcs-database
10ec05e45eab        petitroll/cadvisor   "/usr/bin/cadvisor..."   18 minutes ago      Up 16 seconds             0.0.0.0:8090->8080/tcp     tcs-monitoring
```

### Stopping / Starting a container

A container can be stopped, using the `stop` command. When stopped, the container still exists on the local machine, and can be restarted using the `start` command. Restarting an existing container is obviously quicker than creating a new container.

```
azrael:tmp mosser$ time docker run -d --name my_container petitroll/tcs-rest
128ea953da427b5e6722370a54499f48052b8bd26cec1a4a556da5a3c1dfafa2

real	0m0.849s
user	0m0.013s
sys	0m0.010s
azrael:tmp mosser$ docker stop my_container
my_container
azrael:tmp mosser$ time docker start my_container
my_container

real	0m0.563s
user	0m0.009s
sys	0m0.010s
```

### Removing the container

To remove a stopped container and free resources, the `rm` command will do the job.

```
mosser@azrael service$ docker rm my_container 
```

### Pruning the system

To clean your system of all stopped container, defined networks, dangling images and cache, one can use the `docker system prune` command to automate this task.

```
azrael:tmp mosser$ docker system prune
WARNING! This will remove:
        - all stopped containers
        - all networks not used by at least one container
        - all dangling images
        - all build cache
Are you sure you want to continue? [y/N] y
Deleted Containers:
128ea953da427b5e6722370a54499f48052b8bd26cec1a4a556da5a3c1dfafa2
2fa5bffb2e51b0036dc5ae4a20551511227804628408d407fe1346b41320c0c2
741ae79546ab12fe624fdf1315a023567ab848f58eb5ebd18f354790fe926c73
6766e58c19990b6a4fad5ec5b7d42f9ea4a9359e671fa6fdfe9647208a836ac8
d438214736c125a8c93645a87a8214afce971a2cdc7fc8df4383d61b75035549
e5ab344611276ae81e87d57456a781026e889df1b8281466d98596712f4dc314
3c1bcc154f4eb9cd589fd5f4dcea516655b903bb74d7deac317438a767f9933f
8369832d0b9bf0bae95878a04e894bca35906e54184903b8e7cfa1d03120af75
656966dabc96fc94283a90da12742b6ac84d3f33e6c69f611cd6d76477fde9a4
8d669b6019b15d9486d6821d0173797fd3db4b4811efbec52c3ae1df2bf14190
4374ad69770db89ca31630b7dba61c0a36852aca58e417bcead81f513f0f27ca
d15d54cde9d351a7e09d41f54d4e80a4f19ab4e2cb813e78e2d19ee877347b90

Deleted Images:
untagged: petitroll/tcs-doc@sha256:05164e54b6154d998ae3d738bf76078bb109d54a169badeecae08fa67ee7a72c
deleted: sha256:e2fd9a975f554613dd5c175412fb9092726a1b33040518714e9c04bbf84fd875
deleted: sha256:8648bce79c2c37141fbe1c30c2c672e849fece18cb68a35adeb65bd65e010734
deleted: sha256:b8d033b64b30d76418169dd2dccfbbaa4e997276588a91231bad1b85233e65bf
untagged: petitroll/tcs-rpc@sha256:b4951ac5d1c2720b2dbd56eb682e37aa79d664241e1cb77d510515c78e9e00e8
deleted: sha256:1d69e360a5d2881f98c7f98bfdf41f60f6556bc19d7ecf8ba1dea653225b52bb
deleted: sha256:4347de8432243131e5773e60e292539cc8b4179f74afd062ebe9ee219e7d8471
deleted: sha256:1e9c137949e8830d1cf781b806060a2e6b5e3b311e3c947b5e97dccdac03107e
untagged: petitroll/tcs-rest@sha256:902b7d5187ebadfb32b0bd18d44ef704154699ac02ec2b2de5ba37cdbb39cb05
deleted: sha256:22becb16845d2e452f21e2c7ecb517f1a1d297ce7a71787f8e830a2ba807bafa
deleted: sha256:fb180ef94fd861cef9d05a1f2feefcc2bb99669315ebde15464ead703296e218
deleted: sha256:982e0935132b25511423e55a0bea2d1dad867d66118f6096a8ec8e56db1408d6

Total reclaimed space: 3.595MB
```