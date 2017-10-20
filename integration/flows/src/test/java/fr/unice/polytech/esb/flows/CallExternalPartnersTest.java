package fr.unice.polytech.esb.flows;

import static fr.unice.polytech.esb.flows.utils.Endpoints.*;

import fr.unice.polytech.esb.flows.data.Person;
import fr.unice.polytech.esb.flows.utils.Endpoints;
import org.apache.camel.Exchange;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class CallExternalPartnersTest extends ActiveMQTest {


    /*
                    */

    @Override public String isMockEndpointsAndSkip() {
        return REGISTRATION_ENDPOINT +
                "|" + GENERATOR_ENDPOINT +
                "|" + TAX_COMPUTATION
        ;
    }

    @Override public String isMockEndpoints() {
        return GET_CITIZEN_INFO          +
                "|" + GET_ANONYMOUS_ID    +
                "|" + CREATE_ANONYMOUS_GEN +
                "|" + TAX_COMPUTE_COMPLEX +
                "|" + TAX_COMPUTE_SIMPLE +
                "|" + DEATH_POOL
        ;
    }

    @Test public void testExecutionContext() throws Exception {
        isAvailableAndMocked(REGISTRATION_ENDPOINT);
        isAvailableAndMocked(GENERATOR_ENDPOINT);
        isAvailableAndMocked(TAX_COMPUTATION);
        isAvailableAndMocked(GET_CITIZEN_INFO);
        isAvailableAndMocked(GET_ANONYMOUS_ID);
        isAvailableAndMocked(CREATE_ANONYMOUS_GEN);
        isAvailableAndMocked(TAX_COMPUTE_COMPLEX);
        isAvailableAndMocked(TAX_COMPUTE_SIMPLE);
        isAvailableAndMocked(DEATH_POOL);
    }

    @Before
    public void initMocks() {
        resetMocks();
        mock(REGISTRATION_ENDPOINT).whenAnyExchangeReceived((Exchange exc) -> {
            String template = "{\n" +
                    "    \"address\": \"nowhere, middle of\",\n" +
                    "    \"last_name\": \"Doe\",\n" +
                    "    \"first_name\": \"Jane\",\n" +
                    "    \"zip_code\": \"06543\",\n" +
                    "    \"ssn\": \"1234567890\",\n" +
                    "    \"birth_year\": 1970\n" +
                    "}";
            exc.getIn().setBody(template);
        });
    }

    private Person jane;
    @Before public void initJane() {
        jane = new Person();
        jane.setLastName("Doe"); jane.setFirstName("Jane");
        jane.setAddress("nowhere, middle of");
        jane.setBirthYear("1970"); jane.setZipCode("06543");
        jane.setSsid("1234567890");
    }

    @Test public void testRetrieveCitizenInfo() throws Exception {
        mock(GET_CITIZEN_INFO).expectedMessageCount(1);

        // Calling the integration flow
        Object out = template.requestBody(GET_CITIZEN_INFO, jane.getSsid());

        // ensuring assertions
        assertMockEndpointsSatisfied(1, TimeUnit.SECONDS);
        assertEquals(jane, out);
    }

}
