package fr.unice.polytech.esb.flows;

import fr.unice.polytech.esb.flows.data.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import fr.unice.polytech.esb.flows.utils.CsvFormat;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.Map;
import java.util.concurrent.*;

import static fr.unice.polytech.esb.flows.utils.Endpoints.*;

public class HandleTaxForms extends RouteBuilder {


    private static final ExecutorService WORKERS = Executors.newFixedThreadPool(10);

    @Override public void configure() throws Exception {

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

        from(HANDLE_A_TAX_FORM)
                .routeId("handle-a-tax-form")
                .routeDescription("Handle the tax form of a given citizen")

                .setProperty("tax-form",simple("${body}"))
                .setBody(simple("${body.ssn}"))
                .inOut(GET_CITIZEN_INFO)
                .process((Exchange exchange) -> {
                    Person p = exchange.getIn().getBody(Person.class);
                    TaxForm f = exchange.getProperty("tax-form", TaxForm.class);
                    exchange.getIn().setBody(new TaxInfo(p,f));
                })
                .removeProperty("tax-form")
        ;

        from(CSV_INPUT_FILE_TAXES)
                .routeId("csv-to-tax-form-processing")
                .routeDescription("Loads a CSV file containing tax forms and process contents")

                .log("Processing ${file:name}")
                .unmarshal(CsvFormat.buildCsvFormat())  // Body is now a List of Map<String -> Object>
                .log("  Splitting the content of the file into atomic lines")
                .split(body())
                    .parallelProcessing().executorService(WORKERS)
                    .process(csv2taxForm)
                    .to(HANDLE_A_TAX_FORM)
        ;
    }

    private static Processor csv2taxForm = (Exchange exchange) -> {
        Map<String, Object> data = (Map<String, Object>) exchange.getIn().getBody();
        TaxForm form = new TaxForm();
        form.setSsn((String) data.get("Fodselsnummer"));
        form.setEmail((String) data.get("Epost"));
        form.setPhone((String) data.get("Telefon"));
        form.setIncome(getMoneyValue(data, "Inntekt"));
        form.setIncome(getMoneyValue(data, "Formue"));
        exchange.getIn().setBody(form);
    };

    private static int getMoneyValue(Map<String, Object> data, String field) {
        String rawIncome = (String) data.get(field);
        return Integer.parseInt(rawIncome.replace(",", "").substring(0, rawIncome.length() - 3));
    }

}
