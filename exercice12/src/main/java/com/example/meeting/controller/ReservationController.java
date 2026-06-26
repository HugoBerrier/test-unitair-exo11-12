package com.example.meeting.controller;

import com.example.meeting.dto.CreateReservationRequest;
import com.example.meeting.dto.ReservationResponse;
import com.example.meeting.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReservationResponse.from(reservationService.createReservation(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable Long id) {
        return ResponseEntity.ok(ReservationResponse.from(reservationService.getReservation(id)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(ReservationResponse.from(reservationService.cancelReservation(id)));
    }
}
