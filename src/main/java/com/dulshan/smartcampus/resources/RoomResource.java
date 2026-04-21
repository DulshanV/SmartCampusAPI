package com.dulshan.smartcampus.resources;

import com.dulshan.smartcampus.models.Room;
import com.dulshan.smartcampus.store.DataStore;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
public class RoomResource {

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
        if (DataStore.rooms.containsKey(newRoom.getId())) {
            return Response.status(Response.Status.CONFLICT)
                           .entity("Room ID already exists.")
                           .build();
        }
        
        DataStore.rooms.put(newRoom.getId(), newRoom);
        
        return Response.status(Response.Status.CREATED)
                       .entity(newRoom)
                       .build();
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("id") String id) {
        
        
        Room room = DataStore.rooms.get(id);
        
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Room not found.")
                           .build();
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
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Room not found.")
                           .build();
        }

        // RUBRIC REQUIREMENT: Check if room has sensors attached
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            return Response.status(Response.Status.CONFLICT)
                           .entity("Cannot delete room: It has active sensors attached.")
                           .build();
        }

        DataStore.rooms.remove(id);
        return Response.ok("Room " + id + " deleted successfully.").build();
    }
}