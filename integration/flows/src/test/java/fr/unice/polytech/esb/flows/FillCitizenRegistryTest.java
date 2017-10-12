package fr.unice.polytech.esb.flows;

import fr.unice.polytech.esb.flows.data.Person;
import fr.unice.polytech.esb.flows.utils.Endpoints;
import org.apache.camel.builder.RouteBuilder;
import org.junit.*;
import org.skyscreamer.jsonassert.JSONAssert;

public class FillCitizenRegistryTest extends ActiveMQTest {

    @Override public String isMockEndpointsAndSkip() { return Endpoints.REGISTRATION_ENDPOINT; }

    @Override protected RouteBuilder createRouteBuilder() throws Exception { return new FillCitizenRegistry(); }

    private Person john;

    @Before public void initJohn() {
        john = new Person();
        john.setLastName("Doe");
        john.setFirstName("John");
        john.setAddress("nowhere, middle of");
        john.setBirthYear("1970");
        john.setSsid("1234567890");
        john.setZipCode("06543");
    }

    @Test
    public void testCitizenRegistration() throws Exception {

        // Asserting endpoints existence
        assertNotNull(context.hasEndpoint(Endpoints.REGISTER_A_CITIZEN));
        assertNotNull(context.hasEndpoint(Endpoints.REGISTRATION_ENDPOINT));

        // Configuring expectations on the mocked endpoint
        String mock = "mock://"+Endpoints.REGISTRATION_ENDPOINT;
        assertNotNull(context.hasEndpoint(mock));
        getMockEndpoint(mock).expectedMessageCount(1);
        getMockEndpoint(mock).expectedHeaderReceived("Content-Type", "application/json");
        getMockEndpoint(mock).expectedHeaderReceived("Accept", "application/json");
        getMockEndpoint(mock).expectedHeaderReceived("CamelHttpMethod", "POST");

        // Sending Johm for registration
        template.sendBody(Endpoints.REGISTER_A_CITIZEN, john);

        getMockEndpoint(mock).assertIsSatisfied();

        // As the assertions are now satisfied, one can access to the contents of the exchanges
        String request = getMockEndpoint(mock).getReceivedExchanges().get(0).getIn().getBody(String.class);

        String expected = "{\n" +
                "  \"event\": \"REGISTER\",\n" +
                "  \"citizen\": {\n" +
                "    \"last_name\": \"Doe\",\n" +
                "    \"first_name\": \"John\",\n" +
                "    \"ssn\": \"1234567890\",\n" +
                "    \"zip_code\": \"06543\",\n" +
                "    \"address\": \"nowhere, middle of\",\n" +
                "    \"birth_year\": \"1970\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expected, request, false);
    }

}
