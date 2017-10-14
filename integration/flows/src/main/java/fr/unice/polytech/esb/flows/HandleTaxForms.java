package fr.unice.polytech.esb.flows;

import fr.unice.polytech.esb.flows.data.*;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import static fr.unice.polytech.esb.flows.utils.Endpoints.*;

public class HandleTaxForms extends RouteBuilder {

    @Override public void configure() throws Exception {

        /*****************************************
         ** Business Logic: CSV file -> Letters **
         *****************************************/

        from(BUILD_TAX_INFO)
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

        from(COMPUTE_TAXES)
                .setProperty("partial-tax-info", simple("${body)"))

                .inOut(GET_ANONYMOUS_ID)
                .setProperty("req-uuid", simple("${body}"))
                .setBody(simple("${exchangedProperty[partial-tax-info]}"))

                .choice()
                    .when(simple("${body.form.income} >= 42000"))
                        .setProperty("tax-comp-method", constant("COMPLEX"))
                        .inOut(TAX_COMPUTE_COMPLEX)
                    .when(simple("${body.form.income} >= 0 && ${body.form.income} < 42000"))
                        .setProperty("tax-comp-method", constant("SIMPLE"))
                        .inOut(TAX_COMPUTE_SIMPLE)
                    .otherwise()
                        .to(BAD_CITIZEN).stop() // stopping the route for bad citizens
                .end()
                .removeProperty("partial-tax-info")
        ;

    }

}
