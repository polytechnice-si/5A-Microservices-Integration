package scenarios;

import cucumber.api.java.en.*;
import stubs.tcs.*;

import javax.xml.ws.BindingProvider;
import java.net.URL;

import static org.junit.Assert.*;


public class TaxComputationStepDefinition {

    private String host = "localhost";
    private int port = 8080;

    private String taxPayerId;
    private int taxPayerIncome;
    private int taxPayerAssets;
    private String method;
    private String taxPayerZipCode;

    private TaxComputation_Type response;

    @Given("^The TCS service deployed on (.*):(\\d+)$")
    public void select_host_and_port(String host, int port) { this.host = host; this.port = port; }

    @Given("^a taxpayer identified as (.*)$")
    public void declare_a_taxpayer(String identifier) { this.taxPayerId = identifier; }

    @Given("^an income of (\\d+) kroner$")
    public void declare_an_income(int income) { this.taxPayerIncome = income; }

    @Given("^living in the following area: (.+)$")
    public void declare_an_income(String zip) { this.taxPayerZipCode = zip; }

    @Given("^an assets value of (\\d+) kroner$")
    public void declare_assets(int assets) { this.taxPayerAssets = assets; }

    @When("^the (simple|complex) computation method is selected$")
    public void select_payment_method(String method) { this.method = method; }

    @When("^the service is called$")
    public void call_service() {
        assertTrue("Unknown method",this.method.equals("simple") || this.method.equals("complex"));
        TaxComputation tcs = getWS();
        if(this.method.equals("simple")) {
            SimpleTaxRequest request = new SimpleTaxRequest();
            request.setId(this.taxPayerId);
            request.setIncome(this.taxPayerIncome);
            this.response = tcs.simple(request);
        } else {
            AdvancedTaxRequest request = new AdvancedTaxRequest();
            request.setId(this.taxPayerId);
            request.setIncome(this.taxPayerIncome);
            request.setZone(this.taxPayerZipCode);
            request.setAssets(this.taxPayerAssets);
            this.response = tcs.complex(request);
        }
    }

    @Then("^the computed tax amount is (\\d+\\.\\d+)$")
    public void validate_tax_amount(float value) {
        assertEquals(value, this.response.getAmount(),0.001);
    }

    @Then("^the computation date is set$")
    public void computation_date_is_set() {
        assertNotNull(this.response.getDate());
    }

    @Then("^the answer is associated to (.*)$")
    public void right_identifier_in_the_response(String identifier) {
        assertEquals(identifier,this.response.getIdentifier());
    }

    private TaxComputation getWS() {
        URL wsdl = TaxComputationStepDefinition.class.getResource("ExternalTaxComputerService.wsdl");
        ExternalTaxComputerService factory = new ExternalTaxComputerService(wsdl);
        TaxComputation ws = factory.getExternalTaxComputerPort();
        String address = "http://"+this.host+":"+this.port+"/tcs-service-rpc/ExternalTaxComputerService";
        ((BindingProvider) ws).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
        return ws;
    }


}
