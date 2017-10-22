package fr.unice.polytech.esb.flows;

import fr.unice.polytech.esb.flows.data.Person;
import fr.unice.polytech.esb.flows.data.TaxInfo;
import fr.unice.polytech.esb.flows.utils.TaxComputationHelper;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
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

                .log("Creating retrieval request for citizen #${body}")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("Accept", constant("application/json"))
                .process((Exchange exchange) -> {
                    String request = "{\n" +
                            "  \"event\": \"RETRIEVE\",\n" +
                            "  \"ssn\": \""+ exchange.getIn().getBody(String.class) +"\"\n" +
                            "}";
                    exchange.getIn().setBody(request);
                })
                .inOut(REGISTRATION_ENDPOINT)
                .unmarshal().json(JsonLibrary.Jackson, Person.class)
        ;

        /****************************************************************
         ** Tax Computation (a RPC service implemented using SOAP/XML) **
         ****************************************************************/

        from(TAX_COMPUTE_SIMPLE)
                .routeId("simple-tax-computation-call")
                .routeDescription("Call the tax computation service using the simple method")

                .log("Computing taxes using SIMPLE method for ${body}")
                .bean(TaxComputationHelper.class, "buildSimpleRequest(${body}, ${header[req-uuid]})")
                .inOut(TAX_COMPUTATION)

        ;

        from(TAX_COMPUTE_COMPLEX)
                .routeId("complex-tax-computation-call")
                .routeDescription("Call the tax computation service using the complex method")

                .log("Computing taxes using COMPLEX method for ${body}")
                .bean(TaxComputationHelper.class, "buildAdvancedRequest(${body}, ${header[req-uuid]})")
                .inOut(TAX_COMPUTATION)
        ;

        /*************************************************************************
         ** Data Anonymization (a resource service implemented using REST/JSON) **
         *************************************************************************/

        from(CREATE_ANONYMOUS_GEN)
                .routeId("create-anonymous-generator")
                .routeDescription("Create an anonymous generator")

                .log("Creating an anonymizer")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader("Content-Type", constant("text/plain"))
                .setHeader("Accept", constant("application/json"))

                .setProperty("gen-name", simple("gen-${exchangeId}"))
                .setBody(simple("${exchangeProperty[gen-name]}"))
                .to(GENERATOR_ENDPOINT)
                .setBody(simple(GENERATOR_ENDPOINT.replace("http:", "http://") + "/${exchangeProperty[gen-name]}"))
                .removeProperty("gen-name")
        ;

        from(GET_ANONYMOUS_ID)
                .routeId("compute-anonymous-id")
                .routeDescription("Obtain an anonymous ID from the ID generator")

                .log("Obtaining an UUID from ${header[citizen-id-gen]}")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("Accept", constant("application/json"))
                // Using a dynamic endpoint => refer to a recipient list, inOut as an endpoint parameter
                .setBody(simple(""))
                //.setExchangePattern(ExchangePattern.InOut)
                .recipientList(simple("${header[citizen-id-gen]}"))
                //.setExchangePattern(ExchangePattern.InOnly)
                .unmarshal().json(JsonLibrary.Jackson, String.class)
                .log("UUID: ${body}")
        ;


    }

}
