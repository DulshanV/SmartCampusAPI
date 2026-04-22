package com.dulshan.smartcampus.resources;

import com.dulshan.smartcampus.exceptions.ErrorResponse;
import com.dulshan.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.dulshan.smartcampus.models.Sensor;
import com.dulshan.smartcampus.store.DataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/sensors")
public class SensorResource {

    @Context
    private UriInfo uriInfo;

    private Response jsonError(Response.Status status, String message) {
        ErrorResponse error = new ErrorResponse(
                status.getStatusCode(),
                status.getReasonPhrase(),
                message,
                uriInfo != null ? uriInfo.getPath() : "",
                System.currentTimeMillis());

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(DataStore.sensors.values());

        if (type != null && !type.isEmpty()) {
            sensorList = sensorList.stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }

        return Response.ok(sensorList).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor newSensor) {
        if (newSensor == null || newSensor.getId() == null || newSensor.getId().trim().isEmpty()) {
            return jsonError(Response.Status.BAD_REQUEST, "Sensor payload or ID is missing.");
        }

        if (DataStore.sensors.containsKey(newSensor.getId())) {
            return jsonError(Response.Status.CONFLICT, "Sensor ID already exists.");
        }

        if (newSensor.getRoomId() == null || newSensor.getRoomId().trim().isEmpty()) {
            throw new LinkedResourceNotFoundException("roomId is required.");
        }

        if (!DataStore.rooms.containsKey(newSensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room ID " + newSensor.getRoomId() + " does not exist.");
        }

        DataStore.sensors.put(newSensor.getId(), newSensor);
        DataStore.rooms.get(newSensor.getRoomId()).getSensorIds().add(newSensor.getId());

        URI location = uriInfo.getAbsolutePathBuilder().path(newSensor.getId()).build();
        return Response.created(location).entity(newSensor).build();
    }

    @Path("/{id}/readings")
    public SensorReadingResource getReadingResource(@PathParam("id") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}