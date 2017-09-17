package registry;

import org.jongo.marshall.jackson.oid.MongoObjectId;
import org.json.JSONObject;

public class Citizen {


    private String ssn;
    private String firstName;
    private String lastName;
    private String zipcode;
    private String address;
    private int birthYear;

    @MongoObjectId
    String _id;

    public Citizen() {}

    public Citizen(JSONObject data) {
        this.ssn = data.getString("ssn");
        this.birthYear = data.getInt("birth_year");
        this.lastName = data.getString("last_name");
        this.firstName = data.getString("first_name");
        this.zipcode = data.getString("zip_code");
        this.address = data.getString("address");
    }


    JSONObject toJson() {
        return new JSONObject()
                .put("ssn", ssn)
                .put("birth_year", birthYear)
                .put("last_name", lastName)
                .put("first_name", firstName)
                .put("zip_code", zipcode)
                .put("address", address);
    }

}
