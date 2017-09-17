package scenarios;

import cucumber.api.java.en.*;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.client.WebClient;
import static org.junit.Assert.*;

public class RegistryStepDefinition {


    private String host = "host";
    private int port = 8080;

    private JSONObject citizen;
    private JSONObject answer;
    private String ssn;
    private String filter;
    private String safeWord;

    private JSONObject call(JSONObject request) {
        String raw =
                WebClient.create("http://" + host + ":" + port + "/tcs-service-document/registry")
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .header("Content-Type", MediaType.APPLICATION_JSON)
                        .post(request.toString(), String.class);
        return new JSONObject(raw);
    }

    @Given("^an empty registry deployed on (.*):(\\d+)$")
    public void set_clean_registry(String host, int port) {
        this.host = host;
        this.port = port;
        JSONObject ans = call(new JSONObject().put("event", "PURGE").put("use_with", "caution"));
        assertEquals("done", ans.getString("purge"));
    }

    @Given("^a citizen named (John|Jane) added to the registry$")
    public void upload_preregistered_citizen(String name) {
        JSONObject citizen = new JSONObject();
        if (name.equals("John")) {
            citizen.put("ssn", "111-555-001")
                    .put("last_name", "Doe")
                    .put("first_name", "John")
                    .put("zip_code", "55555")
                    .put("address", "nowhere, middle of")
                    .put("birth_year", 1970);
        } else {
            citizen.put("ssn", "111-555-002")
                    .put("last_name", "Dae")
                    .put("first_name", "Jane")
                    .put("zip_code", "15555")
                    .put("address", "nowhere, middle of")
                    .put("birth_year", 1970);
        }
        JSONObject ans = call(new JSONObject().put("event", "REGISTER").put("citizen", citizen));
        assertEquals(true, ans.getBoolean("inserted"));
    }


    @Given("^A citizen identified as (.*)$")
    public void initialize_a_citizen(String identifier) { citizen = new JSONObject(); citizen.put("ssn", identifier); }

    @Given("^with (.*) set to (.*)$")
    public void add_citizen_attribute(String key, String value) { citizen.put(key.trim(),value);  }

    @Given("^a POI identified as (.*)$")
    public void perso_of_interest_ssn(String ssn) { this.ssn = ssn; }

    @Given("^the (.*) safe word$")
    public void setting_safe_word(String word) { this.safeWord = word; }

    @Given("^a filter set to \"(.*)\"$")
    public void setting_filter(String filter) { this.filter = filter; }

    @When("^the (.*) message is sent$")
    public void call_registry(String message) {
        JSONObject request = new JSONObject();
        switch(message) {
            case "REGISTER":
                request.put("event", message).put("citizen", citizen); break;
            case "RETRIEVE":
                request.put("event", message).put("ssn", ssn); break;
            case "DELETE":
                request.put("event", message).put("ssn", ssn); break;
            case "LIST":
                request.put("event", message).put("filter", filter); break;
            case "DUMP":
                request.put("event", message); break;
            case "PURGE":
                request.put("event", message).put("use_with", safeWord); break;
            default:
                throw new RuntimeException("Unknown message");
        }
        answer = call(request);
        assertNotNull(answer);
    }

    @Then("^the citizen is registered$")
    public void the_citizen_is_registered() {
        assertEquals(true,answer.getBoolean("inserted"));
    }

    @Then("^the citizen is removed$")
    public void the_citizen_is_deleted() {
        assertEquals(true,answer.getBoolean("deleted"));
    }

    @Then("^the (.*) is equals to (.*)$")
    public void check_citizen_content(String key, String value) {
        Object data = answer.getJSONObject("citizen").get(key.trim());
        if(data.getClass().equals(Integer.class)) {
            assertEquals(Integer.parseInt(value.trim()), data);
        } else {
            assertEquals(value.trim(), data);
        }
    }

    @Then("^there (?:are|is) (\\d+) citizen(?:s)? in the registry$")
    public void how_many_citizens_in_the_registry(int expected) {
        JSONObject res = call(new JSONObject().put("event", "DUMP"));
        assertEquals(expected,res.getInt("size"));
    }

    @Then("^the citizen exists$")
    public void the_citizen_exists() {
        JSONObject citizen = new JSONObject(answer.toString());
        answer = new JSONObject().put("citizen", citizen);
    }

    @Then("^the answer contains (\\d+) result(?:s)?$")
    public void the_size_is_good(int expected) {
        assertEquals(expected, answer.getInt("size"));
    }

}
