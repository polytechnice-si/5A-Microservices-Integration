package registry;

import org.jongo.marshall.jackson.oid.MongoObjectId;
import org.json.JSONObject;

public class Citizen {


    private String ssn;
    private String lastName;
    private String firstName;
    private String gender;
    private String birthDate;

    @MongoObjectId
    String _id;


    public Citizen() {}

    public Citizen(JSONObject data) {
        this.ssn = data.getString("ssn");
        this.birthDate = data.getString("birth_date");
        this.lastName = data.getString("last_name");
        this.firstName = data.getString("first_name");
        this.gender = data.getString("gender");
    }


    public JSONObject toJson() {
        return new JSONObject()
                .put("ssn", ssn)
                .put("birth_date", birthDate)
                .put("last_name", lastName)
                .put("first_name", firstName)
                .put("gender", gender);
    }

}
