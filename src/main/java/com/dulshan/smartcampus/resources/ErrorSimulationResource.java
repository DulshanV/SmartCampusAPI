package com.dulshan.smartcampus.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/errors")
public class ErrorSimulationResource {

    @GET
    @Path("/trigger-500")
    public String trigger500() {
        throw new RuntimeException("Intentional test exception for global mapper validation.");
    }
}