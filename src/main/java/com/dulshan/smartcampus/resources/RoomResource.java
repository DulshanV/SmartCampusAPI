package com.dulshan.smartcampus.resources;

import com.dulshan.smartcampus.models.Room;
import com.dulshan.smartcampus.store.DataStore;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
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
}