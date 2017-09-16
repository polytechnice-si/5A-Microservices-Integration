package registry;

import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.json.JSONArray;
import org.json.JSONObject;


class Handler {

    static JSONObject register(JSONObject input) {
        MongoCollection citizens = getCitizens();
        Citizen data = new Citizen(input.getJSONObject("citizen"));
        citizens.insert(data);
        return new JSONObject().put("inserted", true).put("citizen",data.toJson());
    }

    static JSONObject delete(JSONObject input) {
        MongoCollection citizens = getCitizens();
        String ssn = input.getString("ssn");
        Citizen theOne = citizens.findOne("{ssn:#}",ssn).as(Citizen.class);
        if (null == theOne) {
            return new JSONObject().put("deleted", false);
        }
        citizens.remove(new ObjectId(theOne._id));
        return new JSONObject().put("deleted", true);
    }

    static JSONObject list(JSONObject input) {
        MongoCollection citizens = getCitizens();
        String filter = input.getString("filter");
        MongoCursor<Citizen> cursor =
                citizens.find("{lastName: {$regex: #}}", filter).as(Citizen.class);
        JSONArray contents = new JSONArray(); int size = 0;
        while(cursor.hasNext()) {
            contents.put(cursor.next().toJson()); size++;
        }
        return new JSONObject().put("size", size).put("citizens", contents);
    }

    static JSONObject dump(JSONObject input) {
        return list(new JSONObject().put("filter",".*"));
    }

    static JSONObject purge(JSONObject input) {
        MongoCollection citizens = getCitizens();
        if(input.getString("use_with").equals("caution")) {
            citizens.drop();
            return new JSONObject().put("purge", "done");
        }
        throw new RuntimeException("Safe word does not match what is expected!");
    }

    static JSONObject retrieve(JSONObject input) {
        MongoCollection citizens = getCitizens();
        String ssn = input.getString("ssn");
        Citizen theOne = citizens.findOne("{ssn:#}",ssn).as(Citizen.class);
        if (theOne == null) {
            throw new RuntimeException("No match found for " + ssn);
        }
        return theOne.toJson();
    }

    private static MongoCollection getCitizens() {
        MongoClient client = new MongoClient(Network.HOST, Network.PORT);
        return new Jongo(client.getDB(Network.DATABASE)).getCollection(Network.COLLECTION);
    }
}
