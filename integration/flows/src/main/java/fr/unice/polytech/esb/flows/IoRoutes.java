package fr.unice.polytech.esb.flows;

import fr.unice.polytech.esb.flows.data.TaxForm;
import fr.unice.polytech.esb.flows.utils.CsvFormat;
import fr.unice.polytech.esb.flows.utils.TaxMessageGenerator;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static fr.unice.polytech.esb.flows.utils.Endpoints.*;
import static fr.unice.polytech.esb.flows.utils.Endpoints.EMAIL_SENDING;

public class IoRoutes extends RouteBuilder {

    private static final ExecutorService WORKERS = Executors.newFixedThreadPool(10);

    @Override
    public void configure() throws Exception {

        from(CSV_INPUT_FILE_TAXES)
                .routeId("csv-to-tax-form-processing")
                .routeDescription("Loads a CSV file containing tax forms and process contents")

                .log("Creating an anonymizer instance")
                .setProperty("file", simple("${body}"))
                .inOut(CREATE_ANONYMOUS_GEN)
                .setProperty("citizen-id-gen", simple("${body}"))
                .setBody(simple("${exchangedProperty[file]})"))
                .removeProperty("file")

                .log("Processing ${file:name}")
                .unmarshal(CsvFormat.buildCsvFormat())  // Body is now a List of Map<String -> Object>
                .log("  Splitting the content of the file into atomic lines")
                .split(body())
                    .parallelProcessing().executorService(WORKERS)
                    .process(csv2taxForm)
                    .inOut(BUILD_TAX_INFO)
                    .inOut(COMPUTE_TAXES)
                    .to(MESSAGE_GENERATION)
        ;

        from(MESSAGE_GENERATION)
                .routeId("message-generation")
                .routeDescription("Generate a message associated to the tax info retrieved")

                .setProperty("tax-info", simple("${body}"))
                .bean(TaxMessageGenerator.class,
                        "write(${body}, ${exchangeProperty[tax-comp-method]})")

                .multicast()
                .parallelProcessing().executorService(WORKERS)
                .to(SNAIL_MAIL_PRINT, EMAIL_SENDING)
        ;

        from(SNAIL_MAIL_PRINT)
                .to(LETTER_OUTPUT_DIR + "?fileName=${exchangeProperty[tax-info].person.ssn}.txt")
        ;

        from(EMAIL_SENDING)
                .log("Sending email for ${exchangeProperty[tax-info].form.email}")
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
