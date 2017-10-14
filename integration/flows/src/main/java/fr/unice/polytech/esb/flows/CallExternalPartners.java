package fr.unice.polytech.esb.flows;

import fr.unice.polytech.esb.flows.data.Person;
import fr.unice.polytech.esb.flows.data.TaxInfo;
import fr.unice.polytech.esb.flows.utils.TaxComputationHelper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import javax.xml.transform.Source;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import static fr.unice.polytech.esb.flows.utils.Endpoints.*;

public class CallExternalPartners extends RouteBuilder {


    @Override
    public void configure() throws Exception {

        /***********************************************************************
         ** Citizen Registry (a document service implemented using REST/JSON) **
         ***********************************************************************/

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

        /*************************************************************************
         ** Data Anonymization (a resource service implemented using REST/JSON) **
         *************************************************************************/

        from(GET_ANONYMOUS_ID)
                .routeId("compute-anonymous-id")
                .routeDescription("Obtain an anonymous ID from the ID generator")

                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("Accept", constant("application/json"))
                // Using a dynamic endpoint => refer to a recipient list, inOut as an endpoint parameter
                .recipientList(simple("${exchangeProperty[citizen-id-gen]}?exchangePattern=InOut"))
                .unmarshal().json(JsonLibrary.Jackson, String.class)
        ;

        from(CREATE_ANONYMOUS_GEN)
                .routeId("create-anonymous-generator")
                .routeDescription("Create an anonymous generator")

                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Content-Type", constant("text/plain"))
                .setHeader("Accept", constant("application/json"))

                .setProperty("gen-name", simple("gen-${exchangeId}"))
                .setBody(simple("${exchangeProperty[gen-name]}"))
                    .to(GENERATOR_ENDPOINT)
                .setBody(simple(GENERATOR_ENDPOINT + "/${exchangeProperty[gen-name]}"))
                .removeProperty("gen-name")
        ;

        /****************************************************************
         ** Tax Computation (a RPC service implemented using SOAP/XML) **
         ****************************************************************/

        from(TAX_COMPUTE_SIMPLE)
                .routeId("simple-tax-computation-call")
                .routeDescription("Call the tax computation service using the simple method")

                .bean(TaxComputationHelper.class, "buildSimpleRequest(${body}, ${exchangeProperty[req-uuid]})")
                .inOut(TAX_COMPUTATION)
                .process(result2taxInfo)
        ;

        from(TAX_COMPUTE_COMPLEX)
                .routeId("complex-tax-computation-call")
                .routeDescription("Call the tax computation service using the complex method")

                .bean(TaxComputationHelper.class, "buildAdvancedRequest(${body}, ${exchangeProperty[req-uuid]})")
                .inOut(TAX_COMPUTATION)
                .process(result2taxInfo)
        ;

    }

    private static Processor result2taxInfo = (Exchange exc) -> {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Source response = (Source) exc.getIn().getBody();
        TaxInfo result = new TaxInfo(exc.getProperty("partial-tax-info",TaxInfo.class));
        result.setTaxAmount(Float.parseFloat(xpath.evaluate("//amount/text()", response)));
        result.setTimeStamp(xpath.evaluate("//date/text()", response));
        exc.getIn().setBody(result);
    };

}
