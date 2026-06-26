package com.example.meeting.service;

import com.example.meeting.dto.CreateRoomRequest;
import com.example.meeting.model.Room;
import com.example.meeting.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// Service qui gère la création et la liste des salles
@Service
public class DefaultRoomService implements RoomService {

    private final RoomRepository roomRepository;

    public DefaultRoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room createRoom(CreateRoomRequest request) {
        // on crée une nouvelle salle à partir de la requête
        Room room = new Room();
        room.setName(request.getName());
        room.setCapacity(request.getCapacity());
        return roomRepository.save(room);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}
