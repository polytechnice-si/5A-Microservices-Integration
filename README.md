# SOA, Microservices & Integration

  * Author: Sébastien Mosser [(email)](mosser@i3s.unice.fr)
  * Version: 2017.09 
  * Reviewer: [Mireille Blay-Fornarino](blay@i3s.unice.fr)

## Case study: The Tax Computation Service

We consider here a simple case study, the _Tax Computation Service_. The key idea is simple: creating a service-based architecture to support the computation of income taxes for a relatively small country (_i.e._, Norway, 5 millions inhabitants).

### Personas & Stories

  * _Thor_ is a simple tax payer, working as a software engineer in Oslo, the biggest city of the country; As Thor ...
    * I want to receive an email containing my tax return form when my taxes were processed, so that I can know the amount of taxes I paid this year;
    * I want to submit my cousin's name to the system, so that I can see how many taxes he is paying this year.
  * _Anders_ is Thor's cousin. He works as a farmer living in Fredvang, a small village located inside the Lofoten archipelago; As Anders, ...
    * I want my taxes to be handled as a farmer so that I can benefit from the special tax computation associated to this status;
    * I want to receive my tax return form by snail mail, so that I can read it without using a computer;
    * I want to receive a text message  indicating that my tax return was processed. 
  * _Eva_ is working at Skatteetaten, the Norwegian Tax Administration. She is supervising the tax payment process at the kingdom level; As Eva, ...
    * I want to respect the Norwegian law, stating that all processing must be done in an anonymous way; 
    * I want to know how many tax return forms were processed .
  * _Camilla_ is an IT engineer, also working at Skatteetaten. She is ensuring that the computation load will not kill the tax collection process. As Camilla, ...
    * I want to supervise the operational system to monitor how the tax processing is going.

## Development Timeline

### Phase #1: Deploying services

  * Service development
    * [x] [Creating the Tax Computation System as an RPC service](https://github.com/polytechnice-si/5A-Microservices-Integration/blob/master/services/rpc/README.md);
    * [x] [Creating the Anonymous Generator as a Resource service](https://github.com/polytechnice-si/5A-Microservices-Integration/blob/master/services/resource/README.md);
    * [x] [Creating the Citizen Registry as a Document service](https://github.com/polytechnice-si/5A-Microservices-Integration/blob/master/services/document/readme.md).
  * Service deployment
    * [x] [Using containers to deploy services](https://github.com/polytechnice-si/5A-Microservices-Integration/blob/master/deployment/Docker.md);
    * [x] [Composing containers into a global system](https://github.com/polytechnice-si/5A-Microservices-Integration/blob/master/deployment/README.md).
    * [x] [Monitoring containers](https://github.com/polytechnice-si/5A-Microservices-Integration/tree/master/monitoring/README.md)
  * Service testing
    * [x] [Acceptance testing using scenarios](https://github.com/polytechnice-si/5A-Microservices-Integration/blob/master/tests/acceptation/README.md)
    * [x] [Stress testing](https://github.com/polytechnice-si/5A-Microservices-Integration/blob/master/tests/stress/readme.md)

### Phase #2: Integrating services

  * Message broker
    * [ ] Using asynchronous messages to assemble services;
    * [ ] Monitoring the broker 
  * Legacy integration
    * [ ] Leveraging adapters to integrate legacy systems together

## Technological Stack

  * Service Development: 
    * Application server: [TomEE+](http://openejb.apache.org/apache-tomee.html)
    * REST-based service stack: JAX-RS
    * SOAP-based service stack: JAX-WS
  * Integration: 
    * Enterprise Service Bus: [Apache Service Mix](http://servicemix.apache.org/) (7.0.1)
    * Message Broker: [Apache ActiveMQ](http://activemq.apache.org/)
    * Routing: [Apache Camel](http://camel.apache.org/) (2.19.2)
  * Storage: 
    * Database: [MongoDB](https://www.mongodb.com) (3.4)
    * Java Mapping: [Jongo](http://jongo.org/)
  * Deployment: 
    * [Docker Community Engine](https://www.docker.com/community-edition) (17.07.0-rc1)
    * [Docker Compose](https://docs.docker.com/compose/) (1.15.0)
  * Testing:
    * Acceptance testing: [Cucumber](https://cucumber.io/) 
    * Stress testing: [Gatling](http://gatling.io/)
  * Monitoring:
    * ESB: [HawtIO](http://hawt.io/)
    * Services: [cAdvisor](https://github.com/google/cadvisor)  

