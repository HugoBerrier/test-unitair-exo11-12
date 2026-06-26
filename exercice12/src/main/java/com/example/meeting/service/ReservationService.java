package com.example.meeting.service;

import com.example.meeting.dto.CreateReservationRequest;
import com.example.meeting.model.Reservation;

public interface ReservationService {

    Reservation createReservation(CreateReservationRequest request);

    Reservation getReservation(Long id);

    Reservation cancelReservation(Long id);
}
