package com.example.meeting.dto;

import com.example.meeting.model.Room;

public class RoomResponse {

    private final Long id;
    private final String name;
    private final int capacity;

    public RoomResponse(Long id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public static RoomResponse from(Room room) {
        return new RoomResponse(room.getId(), room.getName(), room.getCapacity());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }
}
