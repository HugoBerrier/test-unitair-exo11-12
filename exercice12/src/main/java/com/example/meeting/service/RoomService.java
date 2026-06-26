package com.example.meeting.service;

import com.example.meeting.dto.CreateRoomRequest;
import com.example.meeting.model.Room;

import java.util.List;

public interface RoomService {

    Room createRoom(CreateRoomRequest request);

    List<Room> getAllRooms();
}
