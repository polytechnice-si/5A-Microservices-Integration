# Monitoring containers

  * Author: Sébastien Mosser [(email)](mosser@i3s.unice.fr)
  * Version: 2017.09
  * Reviewer: [Mireille Blay-Fornarino](blay@i3s.unice.fr)

__Warning__: The tool used here for monitoring assumes that the containers run on a Linux/OS X machine.

## How to monitor

We use `cAdvisor`, a too published by google to support container monitoring. The tool is packaged as a container itself. The official image published by Google triggers file system errors, as it lack a proper installation of the `findutils` package. We fixed this by creating our own image that extends the official one and fix the install.

```
FROM google/cadvisor
MAINTAINER Sébastien Mosser (mosser@i3s.unice.fr)
RUN apk add --update findutils && rm -rf /var/cache/apk/*
```

We declare inside the docker-compose file a container for this very image (published as `petitroll/cadvisor`). The image must be linked to the local file system through volumes to gain access to the monitored resources.

```docker
cadvisor:                      
	container_name: tcs-monitoring
    image: petitroll/cadvisor     
    ports:
    	- "8090:8080"
    volumes:
    	- "/:/rootfs:ro"
    	- "/var/run:/var/run:rw"
    	- "/sys:/sys:ro"
    	- "/var/lib/docker/:/var/lib/docker:ro"
```

## Example

<p align="center">
	<img src="https://raw.githubusercontent.com/polytechnice-si/5A-Microservices-Integration/master/monitoring/monitoring_screenshot.png" />
</p>
