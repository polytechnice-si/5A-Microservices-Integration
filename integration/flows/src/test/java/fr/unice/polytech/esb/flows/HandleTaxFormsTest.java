package fr.unice.polytech.esb.flows;

import fr.unice.polytech.esb.flows.data.Person;
import fr.unice.polytech.esb.flows.data.TaxForm;
import fr.unice.polytech.esb.flows.data.TaxInfo;
import fr.unice.polytech.esb.flows.utils.Endpoints;
import static fr.unice.polytech.esb.flows.utils.Endpoints.*;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.junit.*;

import java.util.concurrent.TimeUnit;

import static fr.unice.polytech.esb.flows.DeathPool.REDELIVERIES;


public class HandleTaxFormsTest extends ActiveMQTest {

    @Override public String isMockEndpointsAndSkip() {
        return GET_CITIZEN_INFO          +
                "|" + GET_ANONYMOUS_ID   +
                "|" + TAX_COMPUTE_SIMPLE +
                "|" + TAX_COMPUTE_COMPLEX
        ;
    }

    @Override public String isMockEndpoints() {
        return BAD_CITIZEN          +
                "|" + DEATH_POOL    +
                "|" + COMPUTE_TAXES +
                "|" + BUILD_TAX_INFO
        ;
    }

    @Test public void testExecutionContext() throws Exception {
        isAvailableAndMocked(GET_CITIZEN_INFO);
        isAvailableAndMocked(GET_ANONYMOUS_ID);
        isAvailableAndMocked(TAX_COMPUTE_COMPLEX);
        isAvailableAndMocked(TAX_COMPUTE_SIMPLE);
        isAvailableAndMocked(BAD_CITIZEN);
        isAvailableAndMocked(DEATH_POOL);
        isAvailableAndMocked(COMPUTE_TAXES);
        isAvailableAndMocked(BUILD_TAX_INFO);
    }

    @Before public void initMocks() {
        resetMocks();

        mock(GET_CITIZEN_INFO).whenAnyExchangeReceived((Exchange exc) -> {
            String ssn = exc.getIn().getBody(String.class);
            if (ssn.length() < 2) {
                throw new IllegalArgumentException("Bad citizen-id");
            }
            Person p = new Person(); p.setAddress("nowhere, middle of");
            p.setLastName("Doe");   p.setFirstName("John");
            p.setZipCode("06543");  p.setSsid(ssn);
            p.setBirthYear("1970");
            exc.getIn().setBody(p);
        });

        mock(GET_ANONYMOUS_ID).whenAnyExchangeReceived((Exchange e) -> {  e.getIn().setBody("0987654321"); });

        mock(TAX_COMPUTE_SIMPLE).whenAnyExchangeReceived((Exchange e) -> { e.getIn().setBody(
                    "<ns2:simpleResponse xmlns:ns2=\"http://informatique.polytech.unice.fr/soa1/cookbook/\">\n" +
                    "  <simple_result>\n" +
                    "    <amount>2400.0</amount>\n" +
                    "    <date>Fri Oct 20 17:40:06 UTC 2017</date>\n" +
                    "    <identifier>0987654321</identifier>\n" +
                    "  </simple_result>\n" +
                    "</ns2:simpleResponse>"); });

        mock(TAX_COMPUTE_COMPLEX).whenAnyExchangeReceived((Exchange e) -> { e.getIn().setBody(
                    "<ns2:complexResponse xmlns:ns2=\"http://informatique.polytech.unice.fr/soa1/cookbook/\">\n" +
                    "<complex_result>\n" +
                    "    <amount>7440.0</amount>\n" +
                    "    <date>Fri Oct 20 17:26:04 UTC 2017</date>\n" +
                    "    <identifier>12345</identifier>\n" +
                    "  </complex_result>\n" +
                    "</ns2:complexResponse>" ); });
    }

    private TaxForm form;
    @Before public void initTaxForm() {
        this.form = new TaxForm();
        this.form.setSsn("1234567890");
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

    @Test public void testHandleATaxForm() throws Exception {
        mock(GET_CITIZEN_INFO).expectedMessageCount(1);
        mock(DEATH_POOL).expectedMessageCount(0);

        TaxInfo info = new TaxInfo(john, form);
        TaxInfo out = (TaxInfo) template.requestBody(BUILD_TAX_INFO, form);

        assertMockEndpointsSatisfied(1, TimeUnit.SECONDS);
        assertEquals(info, out);
    }

    @Test public void testHandleABadTaxForm() throws Exception {
        mock(GET_CITIZEN_INFO).expectedMessageCount(1 + REDELIVERIES);
        mock(DEATH_POOL).expectedMessageCount(1 );

        form.setSsn("1");
        template.requestBody(BUILD_TAX_INFO, form);

        assertMockEndpointsSatisfied(1, TimeUnit.SECONDS);
    }

    @Test public void testSimpleForm() throws Exception {
        mock(COMPUTE_TAXES).expectedMessageCount(1);
        mock(GET_ANONYMOUS_ID).expectedMessageCount(1);
        mock(TAX_COMPUTE_COMPLEX).expectedMessageCount(0);
        mock(TAX_COMPUTE_SIMPLE).expectedMessageCount(1);
        mock(BAD_CITIZEN).expectedMessageCount(0);
        mock(DEATH_POOL).expectedMessageCount(0);

        form.setIncome(12000); // Simple method
        TaxInfo info = new TaxInfo(john, form);
        TaxInfo out = (TaxInfo) template.requestBody(COMPUTE_TAXES, info);

        assertMockEndpointsSatisfied(1, TimeUnit.SECONDS);
        assertEquals(2400.0, out.getTaxAmount(),0.001);
        assertNotNull(out.getTimeStamp());
        assertEquals(form, out.getForm());
        assertEquals(john, out.getPerson());
    }

    @Test public void testComplexForm() throws Exception {
        mock(COMPUTE_TAXES).expectedMessageCount(1);
        mock(GET_ANONYMOUS_ID).expectedMessageCount(1);
        mock(TAX_COMPUTE_COMPLEX).expectedMessageCount(1);
        mock(TAX_COMPUTE_SIMPLE).expectedMessageCount(0);
        mock(BAD_CITIZEN).expectedMessageCount(0);
        mock(DEATH_POOL).expectedMessageCount(0);

        form.setIncome(42000); // complex method
        TaxInfo info = new TaxInfo(john, form);
        TaxInfo out = (TaxInfo) template.requestBody(COMPUTE_TAXES, info);

        assertMockEndpointsSatisfied(1, TimeUnit.SECONDS);
        assertEquals(7440, out.getTaxAmount(),0.001);
        assertNotNull(out.getTimeStamp());
        assertEquals(form, out.getForm());
        assertEquals(john, out.getPerson());
    }

    @Test public void testBadForm() throws Exception {
        mock(COMPUTE_TAXES).expectedMessageCount(1);
        mock(GET_ANONYMOUS_ID).expectedMessageCount(1);
        mock(TAX_COMPUTE_COMPLEX).expectedMessageCount(0);
        mock(TAX_COMPUTE_SIMPLE).expectedMessageCount(0);
        mock(BAD_CITIZEN).expectedMessageCount(1);
        mock(DEATH_POOL).expectedMessageCount(0);

        form.setIncome(-1); // complex method
        TaxInfo info = new TaxInfo(john, form);
        template.requestBody(COMPUTE_TAXES, info);

        assertMockEndpointsSatisfied(1, TimeUnit.SECONDS);
    }

}