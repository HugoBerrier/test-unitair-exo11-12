package com.example.meeting.repository;

import com.example.meeting.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    Optional<Reservation> findById(Long id);

    List<Reservation> findConfirmedByRoomId(Long roomId);
}
