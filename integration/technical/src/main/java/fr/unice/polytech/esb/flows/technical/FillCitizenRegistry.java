package fr.unice.polytech.esb.flows.technical;

import fr.unice.polytech.esb.flows.technical.data.Person;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.CsvDataFormat;

import java.util.Map;

public class FillCitizenRegistry extends RouteBuilder {


    private static final String CSV_INPUT_DIRECTORY = "file:/servicemix/camel/input";
    private static final String REGISTER_A_CITIZEN = "activemq:registerCitizen";
    private static final String REGISTRATION_ENDPOINT = "http://tcs-citizens:9080/tcs-service-document/registry";

    @Override
    public void configure() throws Exception {

        from(CSV_INPUT_DIRECTORY)
                .routeId("csv-to-citizen-registration")
                .routeDescription("Loads a CSV file containing citizens and routes contents to the Registry")
                .log("Processing ${file:name}")
                .log("  Loading the file as a CSV document")
                .unmarshal(buildCsvFormat())  // Body is now a List(Map("navn" -> ..., ...), ...)
                .log("  Splitting the content of the file into atomic lines")
                .split(body())
                .log("  Transforming a CSV line into a Person")
                .process(csv2person)
                .log("  Transferring to the route that handle a given citizen")
                .to(REGISTER_A_CITIZEN)   // Async transfer with JMS ( activemq:... )
        ;

        from(REGISTER_A_CITIZEN)
                .routeId("calling-registry")
                .routeDescription("Send POST requests to the Citizen Registry, reading from an ActiveMQ")
                .log("Processing ${body.lastName}")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("Accept", constant("application/json"))
                .process(person2registration)
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
            exchange.getIn().setBody(p);
    };

    private static Processor person2registration = (Exchange exchange) -> {
        Person p = (Person) exchange.getIn().getBody();
        String request = "{ \"event\": \"REGISTER\"," +
                    "\"citizen\": { " +
                        "\"last_name\": \""     + p.getLastName() + "\"" +
                        "\"first_name\": \""    + p.getFirstName() + "\"" +
                        "\"ssn\": \""           + p.getSsid() + "\"" +
                        "\"zip_code\": \""      + p.getZipCode() + "\"" +
                        "\"address\": \""       + p.getAddress() + "\"" +
                        "\"birth_year\": \""    + p.getBirthYear() + "\"" +
                    "}" +
                "}"
                ;
        exchange.getIn().setBody(request);
    };

}
