package com.dulshan.smartcampus.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscoveryInfo() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("apiName", "Smart Campus API");
        metadata.put("description", "REST API for rooms, sensors, and sensor readings");
        metadata.put("version", "v1");
        metadata.put("adminContact", "admin@smartcampus.edu");
        metadata.put("self", "/api/v1");
        metadata.put("serverTime", System.currentTimeMillis());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("rooms", "/api/v1/rooms");
        endpoints.put("sensors", "/api/v1/sensors");
        endpoints.put("sensorFilterExample", "/api/v1/sensors?type=Temperature");
        
        metadata.put("endpoints", endpoints);

        return Response.ok(metadata).build();
    }
}