package dtu.services.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/ping")
public class TestResource
{
    @GET
    public Response ping()
    {
        return Response.ok("DTU Pay Service is online").build();
    }
}
