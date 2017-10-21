package fr.unice.polytech.esb.flows;

import static fr.unice.polytech.esb.flows.utils.Endpoints.*;

import fr.unice.polytech.esb.flows.data.Person;
import fr.unice.polytech.esb.flows.data.TaxForm;
import fr.unice.polytech.esb.flows.data.TaxInfo;
import org.apache.camel.Exchange;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPathConstants;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

public class CallExternalPartnersTest extends ActiveMQTest {

    @Override public String isMockEndpointsAndSkip() {
        return REGISTRATION_ENDPOINT + "|" + TAX_COMPUTATION
        ;
    }

    @Override public String isMockEndpoints() {
        return GET_CITIZEN_INFO          +
                "|" + TAX_COMPUTE_COMPLEX +
                "|" + TAX_COMPUTE_SIMPLE +
                "|" + DEATH_POOL
        ;
    }

    @Test public void testExecutionContext() throws Exception {
        isAvailableAndMocked(REGISTRATION_ENDPOINT);
        isAvailableAndMocked(TAX_COMPUTATION);
        isAvailableAndMocked(GET_CITIZEN_INFO);
        isAvailableAndMocked(TAX_COMPUTE_COMPLEX);
        isAvailableAndMocked(TAX_COMPUTE_SIMPLE);
        isAvailableAndMocked(DEATH_POOL);
    }

    @Before public void initMocks() {
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

        mock(TAX_COMPUTATION).whenAnyExchangeReceived((Exchange e) -> {
            String response = "";
            InputSource src =  new InputSource(new StringReader(e.getIn().getBody(String.class)));

            boolean isComplex = (boolean) xpath.evaluate("//complexTaxInfo",src, XPathConstants.BOOLEAN);
            if (isComplex) {
                response = "<ns2:complexResponse xmlns:ns2=\"http://informatique.polytech.unice.fr/soa1/cookbook/\">\n" +
                        "  <complex_result>\n" +
                        "    <amount>7440.0</amount>\n" +
                        "    <date>Fri Oct 20 17:26:04 UTC 2017</date>\n" +
                        "    <identifier>12345</identifier>\n" +
                        "  </complex_result>\n" +
                        "</ns2:complexResponse>";
            } else {
                src =  new InputSource(new StringReader(e.getIn().getBody(String.class)));
                boolean isSimple = (boolean) xpath.evaluate("//simpleTaxInfo",src, XPathConstants.BOOLEAN);
                if (isSimple) {
                    response = "<ns2:simpleResponse xmlns:ns2=\"http://informatique.polytech.unice.fr/soa1/cookbook/\">\n" +
                            "  <simple_result>\n" +
                            "    <amount>2400.0</amount>\n" +
                            "    <date>Fri Oct 20 17:40:06 UTC 2017</date>\n" +
                            "    <identifier>12345</identifier>\n" +
                            "  </simple_result>\n" +
                            "</ns2:simpleResponse>";
                } else {
                    throw new IllegalArgumentException("Unknown query" + e.getIn().getBody(String.class));
                }
            }
            e.getIn().setBody(response);
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

    private TaxForm form;
    @Before public void initTaxForm() {
        this.form = new TaxForm();
        this.form.setSsn("1234567890");
        this.form.setIncome(12000);
        this.form.setAssets(42000);
        this.form.setEmail("bar@foo.com");
        this.form.setPhone("123-555-789");
    }

    @Test public void testRetrieveCitizenInfo() throws Exception {
        mock(GET_CITIZEN_INFO).expectedMessageCount(1);
        mock(REGISTRATION_ENDPOINT).expectedMessageCount(1);
        mock(DEATH_POOL).expectedMessageCount(0);
        // Calling the integration flow
        Person out = template.requestBody(GET_CITIZEN_INFO, jane.getSsid(), Person.class);

        // ensuring assertions
        assertMockEndpointsSatisfied(1, TimeUnit.SECONDS);
        assertEquals(jane, out);
    }

    @Test public void testTaxComputeSimple() throws Exception  {
        mock(TAX_COMPUTE_SIMPLE).expectedMessageCount(1);
        mock(TAX_COMPUTATION).expectedMessageCount(1);
        mock(DEATH_POOL).expectedMessageCount(0);

        String out = template.requestBodyAndHeader(
                TAX_COMPUTE_SIMPLE, new TaxInfo(jane, form),
                "req-uuid", "0987654321",
                String.class);

        assertMockEndpointsSatisfied(1, TimeUnit.SECONDS);
        InputSource src =  new InputSource(new StringReader(out));
        assertTrue((boolean) xpath.evaluate("//simple_result", src, XPathConstants.BOOLEAN));
    }

    @Test public void testTaxComputeComplex() throws Exception  {
        mock(TAX_COMPUTE_COMPLEX).expectedMessageCount(1);
        mock(TAX_COMPUTATION).expectedMessageCount(1);
        mock(DEATH_POOL).expectedMessageCount(0);

        String out = template.requestBodyAndHeader(
                TAX_COMPUTE_COMPLEX, new TaxInfo(jane, form),
                "req-uuid", "0987654321",
                String.class);

        assertMockEndpointsSatisfied(1, TimeUnit.SECONDS);
        InputSource src =  new InputSource(new StringReader(out));
        assertTrue((boolean) xpath.evaluate("//complex_result", src, XPathConstants.BOOLEAN));
    }

}
