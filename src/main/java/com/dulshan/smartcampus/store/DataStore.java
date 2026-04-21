package com.dulshan.smartcampus.store;

import com.dulshan.smartcampus.models.Room;
import com.dulshan.smartcampus.models.Sensor;
import com.dulshan.smartcampus.models.SensorReading;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {

    public static Map<String, Room> rooms = new HashMap<>();
    public static Map<String, Sensor> sensors = new HashMap<>();
    public static Map<String, List<SensorReading>> sensorReadings = new HashMap<>();

    static {
        Room room1 = new Room("ROOM-101", "Main Lecture Hall", 200);
        rooms.put(room1.getId(), room1);

        Sensor sensor1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 24.5, "ROOM-101");
        sensors.put(sensor1.getId(), sensor1);
        
        room1.getSensorIds().add(sensor1.getId());
    }
}