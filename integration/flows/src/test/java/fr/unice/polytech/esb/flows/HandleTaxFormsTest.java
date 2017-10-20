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

    @Override public String isMockEndpointsAndSkip() {
        return Endpoints.GET_CITIZEN_INFO          +
                "|" + Endpoints.GET_ANONYMOUS_ID   +
                "|" + Endpoints.TAX_COMPUTE_SIMPLE +
                "|" + Endpoints.TAX_COMPUTE_COMPLEX
        ;
    }

    @Override public String isMockEndpoints() {
        return Endpoints.BAD_CITIZEN          +
                "|" + Endpoints.DEATH_POOL    +
                "|" + Endpoints.COMPUTE_TAXES +
                "|" + Endpoints.BUILD_TAX_INFO
        ;
    }

    @Test public void testExecutionContext() throws Exception {
        isAvailableAndMocked(Endpoints.GET_CITIZEN_INFO);
        isAvailableAndMocked(Endpoints.GET_ANONYMOUS_ID);
        isAvailableAndMocked(Endpoints.TAX_COMPUTE_COMPLEX);
        isAvailableAndMocked(Endpoints.TAX_COMPUTE_SIMPLE);
        isAvailableAndMocked(Endpoints.BAD_CITIZEN);
        isAvailableAndMocked(Endpoints.DEATH_POOL);
        isAvailableAndMocked(Endpoints.COMPUTE_TAXES);
        isAvailableAndMocked(Endpoints.BUILD_TAX_INFO);
    }

    @Before public void initMocks() {
        mock(Endpoints.GET_CITIZEN_INFO).whenAnyExchangeReceived((Exchange exc) -> {
            String ssn = exc.getIn().getBody(String.class);
            if (ssn.length() < 2) {
                throw new IllegalArgumentException("Bad citizen-id");
            }
            Person p = new Person();
            p.setAddress("nowhere, middle of");
            p.setLastName("Doe");
            p.setFirstName("John");
            p.setZipCode("06543");
            p.setSsid(ssn);
            p.setBirthYear("1970");
            exc.getIn().setBody(p);
        });
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

    @Test public void testHandleATaxForm() throws Exception {
        mock(Endpoints.GET_CITIZEN_INFO).expectedMessageCount(1);
        mock(Endpoints.DEATH_POOL).expectedMessageCount(0);

        TaxInfo info = new TaxInfo(john, form);
        TaxInfo out = (TaxInfo) template.requestBody(Endpoints.BUILD_TAX_INFO, form);

        assertMockEndpointsSatisfied(1, TimeUnit.SECONDS);
        assertEquals(info, out);
    }

    @Test public void testHandleABadTaxForm() throws Exception {
        mock(Endpoints.GET_CITIZEN_INFO).expectedMessageCount(1 + REDELIVERIES);
        mock(Endpoints.DEATH_POOL).expectedMessageCount(1 );

        form.setSsn("1");
        template.requestBody(Endpoints.BUILD_TAX_INFO, form);

        assertMockEndpointsSatisfied(1, TimeUnit.SECONDS);
    }

    @Test public void testComputeTaxeSimple() throws Exception {

    }

}