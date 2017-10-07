package fr.unice.polytech.esb.flows.technical;

import fr.unice.polytech.esb.flows.technical.data.Person;
import fr.unice.polytech.esb.flows.technical.data.Request;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FillCitizenRegistry extends RouteBuilder {

    private static final String CSV_INPUT_DIRECTORY   = "file:/servicemix/camel/input";
    private static final String REGISTER_A_CITIZEN    = "activemq:registerCitizen";
    private static final String REGISTRATION_ENDPOINT = "http://tcs-citizens:8080/tcs-service-document/registry";

    private static final ExecutorService WORKERS = Executors.newFixedThreadPool(5);

    @Override
    public void configure() throws Exception {

        from(CSV_INPUT_DIRECTORY)
                .routeId("csv-to-citizen-registration")
                .routeDescription("Loads a CSV file containing citizens and routes contents to the Registry")
                    .log("Processing ${file:name}")
                    .log("  Loading the file as a CSV document")
                .unmarshal(buildCsvFormat())  // Body is now a List of Map<String -> Object>
                    .log("  Splitting the content of the file into atomic lines")
                .split(body())
                    .parallelProcessing().executorService(WORKERS)
                        .log("  Transforming a CSV line into a Person")
                        .process(csv2person)
                        .log("[${body.ssid}]  Transferring to the route that handle a citizen")
                        .to(REGISTER_A_CITIZEN)   // Async transfer with JMS ( activemq:... )
        ;

        from(REGISTER_A_CITIZEN)
                .routeId("calling-registry")
                .routeDescription("Send POST requests to the Citizen Registry, reading from an ActiveMQ")
                    .log("[${body.ssid}] Creating registration request")
                .process((Exchange exc) -> {
                    Request req = new Request("REGISTER",(Person) exc.getIn().getBody());
                    exc.getIn().setBody(req);
                })
                    .log("[${body.citizen.ssid}] Setting headers for the HTTP request")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("Accept", constant("application/json"))
                    .log("[${body.citizen.ssid}] Marshalling request into a JSON body")
                .marshal().json(JsonLibrary.Jackson)
                .to(REGISTRATION_ENDPOINT)
        ;

    }

    private static CsvDataFormat buildCsvFormat() {
        CsvDataFormat format = new CsvDataFormat();
        format.setDelimiter(",");
        format.setSkipHeaderRecord(true);
        format.setUseMaps(true);
        return format;
    }

    private static Processor csv2person = (Exchange exchange) -> {
            Map<String, Object> data = (Map<String, Object>) exchange.getIn().getBody();
            Person p =  new Person();
            String name =  (String) data.get("Navn");
            p.setFirstName((name.split(",")[1].trim()));
            p.setLastName((name.split(",")[0].trim()));
            p.setZipCode((String) data.get("Postnummer"));
            p.setAddress((String) data.get("Postaddressen"));
            p.setSsid((String) data.get("Fodselsnummer"));
            p.setBirthYear((String) data.get("Fodselar"));
            exchange.getIn().setBody(p);
    };

}
