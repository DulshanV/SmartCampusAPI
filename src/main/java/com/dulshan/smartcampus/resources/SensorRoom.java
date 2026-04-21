package com.dulshan.smartcampus.resources;

import com.dulshan.smartcampus.exceptions.ErrorResponse;
import com.dulshan.smartcampus.exceptions.RoomNotEmptyException;
import com.dulshan.smartcampus.models.Room;
import com.dulshan.smartcampus.store.DataStore;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/rooms")
public class SensorRoom {

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
       
        List<Room> roomList = new ArrayList<>(DataStore.rooms.values());
        
        // 2. Package them into an HTTP Response and send them out
        return Response.ok(roomList).build();
        
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room newRoom) {
        if (newRoom == null || newRoom.getId() == null || newRoom.getId().trim().isEmpty()) {
            return jsonError(Response.Status.BAD_REQUEST, "Room payload or ID is missing.");
        }

        if (DataStore.rooms.containsKey(newRoom.getId())) {
            return jsonError(Response.Status.CONFLICT, "Room ID already exists.");
        }

        if (newRoom.getSensorIds() == null) {
            newRoom.setSensorIds(new ArrayList<>());
        }
        
        DataStore.rooms.put(newRoom.getId(), newRoom);

        URI location = uriInfo.getAbsolutePathBuilder().path(newRoom.getId()).build();
        
        return Response.created(location)
                       .entity(newRoom)
                       .build();
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("id") String id) {
        
        
        Room room = DataStore.rooms.get(id);
        
        if (room == null) {
            return jsonError(Response.Status.NOT_FOUND, "Room not found.");
        }
        
        // 3. If it does exist, hand the room back to the user
        return Response.ok(room).build();
    }
    
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("id") String id) {
        Room room = DataStore.rooms.get(id);

        if (room == null) {
            return jsonError(Response.Status.NOT_FOUND, "Room not found.");
        }

        // RUBRIC REQUIREMENT: Check if room has sensors attached
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete room: It has active sensors attached.");
        }

        DataStore.rooms.remove(id);

        Map<String, Object> result = new HashMap<>();
        result.put("status", 200);
        result.put("message", "Room " + id + " deleted successfully.");
        result.put("timestamp", System.currentTimeMillis());

        return Response.ok(result).build();
    }
}