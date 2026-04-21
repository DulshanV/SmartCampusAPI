package com.dulshan.smartcampus.resources;

import com.dulshan.smartcampus.exceptions.ErrorResponse;
import com.dulshan.smartcampus.exceptions.SensorUnavailableException;
import com.dulshan.smartcampus.models.SensorReading;
import com.dulshan.smartcampus.store.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SensorReadingResource {

    private String sensorId;

    @Context
    private UriInfo uriInfo;

    private Response jsonError(Response.Status status, String message) {
        ErrorResponse error = new ErrorResponse(
                status.getStatusCode(),
                status.getReasonPhrase(),
                message,
                uriInfo != null ? uriInfo.getPath() : "",
                System.currentTimeMillis()
        );

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getHistory() {
        return DataStore.sensorReadings.getOrDefault(sensorId, new ArrayList<>());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        if (reading == null) {
            return jsonError(Response.Status.BAD_REQUEST, "Reading payload is missing.");
        }

        if (!DataStore.sensors.containsKey(sensorId)) {
            return jsonError(Response.Status.NOT_FOUND, "Sensor not found.");
        }

        if ("MAINTENANCE".equalsIgnoreCase(DataStore.sensors.get(sensorId).getStatus())) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is under maintenance and cannot accept new readings.");
        }

        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }

        reading.setTimestamp(System.currentTimeMillis());

        // 1. Save to history
        DataStore.sensorReadings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);

        // 2. Update parent Sensor's current value (Rubric Requirement 4.2)
        if (DataStore.sensors.containsKey(sensorId)) {
            DataStore.sensors.get(sensorId).setCurrentValue(reading.getValue());
        }

        URI location = uriInfo.getAbsolutePathBuilder().path(reading.getId()).build();
        return Response.created(location).entity(reading).build();
    }
}