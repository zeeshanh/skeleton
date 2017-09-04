package controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/netid")
public class NetIDController {

    @GET
    public String netID(){
        return "zh278";
    }
}
