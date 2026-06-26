package com.example.meeting.controller;

import com.example.meeting.dto.CreateRoomRequest;
import com.example.meeting.dto.RoomResponse;
import com.example.meeting.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Controller REST pour gérer les salles de réunion
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // POST /api/rooms : créer une salle
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RoomResponse.from(roomService.createRoom(request)));
    }

    // GET /api/rooms : lister toutes les salles
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        List<RoomResponse> rooms = roomService.getAllRooms().stream()
                .map(RoomResponse::from)
                .toList();
        return ResponseEntity.ok(rooms);
    }
}
