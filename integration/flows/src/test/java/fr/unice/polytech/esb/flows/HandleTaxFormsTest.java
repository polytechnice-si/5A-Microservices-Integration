package fr.unice.polytech.esb.flows;

import fr.unice.polytech.esb.flows.data.Person;
import fr.unice.polytech.esb.flows.data.TaxForm;
import fr.unice.polytech.esb.flows.data.TaxInfo;
import fr.unice.polytech.esb.flows.utils.Endpoints;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.junit.*;

import java.util.concurrent.TimeUnit;

import static fr.unice.polytech.esb.flows.DeathPool.REDELIVERIES;

public class HandleTaxFormsTest extends ActiveMQTest {

    @Override public String isMockEndpointsAndSkip() { return Endpoints.REGISTRATION_ENDPOINT;  }
    @Override public String isMockEndpoints() {
        return Endpoints.GET_CITIZEN_INFO +
                "|" + Endpoints.BUILD_TAX_INFO +
                "|" + Endpoints.DEATH_POOL
        ;
    }

    @Override protected RouteBuilder createRouteBuilder() throws Exception {
        HandleTaxForms routes = new HandleTaxForms();
        CallExternalPartners partners = new CallExternalPartners();
        DeathPool deathPool = new DeathPool();

        return new RouteBuilder() {
            @Override public void configure() throws Exception {
                this.includeRoutes(deathPool);
                this.setErrorHandlerBuilder(deathPool.getErrorHandlerBuilder());
                this.includeRoutes(partners);
                this.includeRoutes(routes);
            }
        };
    }

    private static final String citizenRegistry = "mock://"+Endpoints.REGISTRATION_ENDPOINT;
    private static final String getCitizenInfo  = "mock://"+Endpoints.GET_CITIZEN_INFO;
    private static final String handleTaxForm  = "mock://"+Endpoints.BUILD_TAX_INFO;
    private static final String deadLetter = "mock://"+Endpoints.DEATH_POOL;

    @Test public void testExecutionContext() throws Exception {
        assertNotNull(context.hasEndpoint(Endpoints.GET_CITIZEN_INFO));
        assertNotNull(context.hasEndpoint(Endpoints.REGISTRATION_ENDPOINT));
        assertNotNull(context.hasEndpoint(Endpoints.BUILD_TAX_INFO));
        assertNotNull(context.hasEndpoint(Endpoints.DEATH_POOL));
        assertNotNull(context.hasEndpoint(citizenRegistry));
        assertNotNull(context.hasEndpoint(getCitizenInfo));
        assertNotNull(context.hasEndpoint(handleTaxForm));
        assertNotNull(context.hasEndpoint(deadLetter));
    }

    private TaxForm form;
    @Before public void initTaxForm() {
        this.form = new TaxForm();
        this.form.setSsn("1234567890");
        this.form.setIncome(12000);
        this.form.setAssets(42000);
        this.form.setEmail("foo@bar.com");
        this.form.setPhone("123-555-789");
    }

    private Person john;
    @Before public void initJohn() {
        john = new Person();
        john.setLastName("Doe"); john.setFirstName("John");
        john.setAddress("nowhere, middle of");
        john.setBirthYear("1970"); john.setZipCode("06543");
        john.setSsid("1234567890");
    }


    @Before public void initMocks() {
        resetMocks();
        getMockEndpoint(citizenRegistry).whenAnyExchangeReceived((Exchange exc) -> {
            String ssn = exc.getProperty("citizen-id", String.class);
            if (ssn.length() < 2)
                throw new IllegalArgumentException("Bad citizen-id");
            String template = "{\n" +
                    "    \"address\": \"nowhere, middle of\",\n" +
                    "    \"last_name\": \"Doe\",\n" +
                    "    \"first_name\": \"John\",\n" +
                    "    \"zip_code\": \"06543\",\n" +
                    "    \"ssn\": \""+ ssn +"\",\n" +
                    "    \"birth_year\": 1970\n" +
                    "}";
            exc.getIn().setBody(template);
        });
    }

    @Test public void testHandleATaxForm() throws Exception {
        getMockEndpoint(handleTaxForm).expectedMessageCount(1);
        getMockEndpoint(citizenRegistry).expectedMessageCount(1);
        getMockEndpoint(getCitizenInfo).expectedMessageCount(1);
        getMockEndpoint(deadLetter).expectedMessageCount(0);

        TaxInfo info = new TaxInfo(john, form);
        Object out = template.requestBody(Endpoints.BUILD_TAX_INFO, form);

        assertMockEndpointsSatisfied(1, TimeUnit.SECONDS);
        assertEquals(info, out);
    }

    @Test public void testHandleABadTaxForm() throws Exception {
        getMockEndpoint(handleTaxForm).expectedMessageCount(1);
        getMockEndpoint(getCitizenInfo).expectedMessageCount(1);
        getMockEndpoint(citizenRegistry).expectedMessageCount(1 + REDELIVERIES);
        getMockEndpoint(deadLetter).expectedMessageCount(1);

        form.setSsn("1");
        TaxInfo out = (TaxInfo) template.requestBody(Endpoints.BUILD_TAX_INFO, form);

        assertMockEndpointsSatisfied(1, TimeUnit.SECONDS);
        assertNull( out.getPerson());
    }

    @Test public void testRetrieveCitizenInfo() throws Exception {
        // Assertions on the mocks w.r.t. number of exchnaged messages
        getMockEndpoint(citizenRegistry).expectedMessageCount(1);
        getMockEndpoint(getCitizenInfo).expectedMessageCount(1);

        // Calling the integration flow
        Object out = template.requestBody(Endpoints.GET_CITIZEN_INFO, john.getSsid());

        // ensuring assertions
        assertMockEndpointsSatisfied(1, TimeUnit.SECONDS);
        assertEquals(john, out);
    }

}