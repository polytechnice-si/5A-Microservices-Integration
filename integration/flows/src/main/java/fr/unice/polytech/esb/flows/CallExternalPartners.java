package fr.unice.polytech.esb.flows;

import fr.unice.polytech.esb.flows.data.Person;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import static fr.unice.polytech.esb.flows.DeathPool.REDELIVERIES;
import static fr.unice.polytech.esb.flows.utils.Endpoints.DEATH_POOL;
import static fr.unice.polytech.esb.flows.utils.Endpoints.GET_CITIZEN_INFO;
import static fr.unice.polytech.esb.flows.utils.Endpoints.REGISTRATION_ENDPOINT;

public class CallExternalPartners extends RouteBuilder {


    @Override
    public void configure() throws Exception {

        from(GET_CITIZEN_INFO)
                .routeId("retrieve-citizen-information")
                .routeDescription("Retrieve a given citizen based on his/her SSN")

                .setProperty("citizen-id", simple("${body}"))
                .log("Creating retrieval request for citizen #${exchangeProperty[citizen-id]}")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("Accept", constant("application/json"))
                .process((Exchange exchange) -> {
                    String request = "{\n" +
                            "  \"event\": \"RETRIEVE\",\n" +
                            "  \"ssn\": \""+ exchange.getProperty("citizen-id", String.class) +"\"\n" +
                            "}";
                    exchange.getIn().setBody(request);
                })
                .inOut(REGISTRATION_ENDPOINT)
                .unmarshal().json(JsonLibrary.Jackson, Person.class)
        ;


    }
}
