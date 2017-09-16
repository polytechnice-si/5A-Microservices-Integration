package gen;

import org.json.JSONArray;
import org.json.JSONString;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/generators")
// Here we generate JSON data from scratch, one should use a framework instead
@Produces(MediaType.APPLICATION_JSON)
public class GeneratorService {

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public Response createNewGenerator(String name) {
	    if(Storage.read(name) != null) {
			return Response.status(Response.Status.CONFLICT)
					       .entity("\"Existing name " + name + "\"")
					       .build();
		}
		Storage.create(name);
		return Response.ok().build();
	}

	@GET
	public Response getAvailableGenerators() {
		Collection<Generator> gens = Storage.findAll();
		JSONArray result = new JSONArray();
		for(Generator g: gens) {
			result.put(g.getName());
		}
		return Response.ok().entity(result.toString(2)).build();
	}


	@Path("/{name}")
	@GET
	public Response generateIdentifier(@PathParam("name") String name) {
		if(Storage.read(name) == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		String value = Storage.read(name).run();
		return Response.ok().entity("\""+value+"\"").build();
	}

	@Path("/{name}")
	@DELETE
	public Response deleteGenerator(@PathParam("name") String name) {
		if(Storage.read(name) == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		Storage.delete(name);
		return Response.ok().build();
	}

}
