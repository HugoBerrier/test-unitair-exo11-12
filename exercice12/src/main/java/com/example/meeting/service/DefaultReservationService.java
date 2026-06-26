package com.example.meeting.service;

import com.example.meeting.dto.CreateReservationRequest;
import com.example.meeting.exception.InvalidTimeSlotException;
import com.example.meeting.exception.ReservationAlreadyCancelledException;
import com.example.meeting.exception.ReservationConflictException;
import com.example.meeting.exception.ReservationNotFoundException;
import com.example.meeting.exception.RoomNotFoundException;
import com.example.meeting.model.Reservation;
import com.example.meeting.model.ReservationStatus;
import com.example.meeting.repository.ReservationRepository;
import com.example.meeting.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// Service qui contient la logique métier des réservations
@Service
public class DefaultReservationService implements ReservationService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public DefaultReservationService(RoomRepository roomRepository,
                                     ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Reservation createReservation(CreateReservationRequest request) {
        // on vérifie que la salle existe
        roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(request.getRoomId()));

        // la fin doit être après le début
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new InvalidTimeSlotException();
        }

        // on vérifie qu'il n'y a pas de chevauchement avec une autre réservation
        boolean hasConflict = reservationRepository.findConfirmedByRoomId(request.getRoomId()).stream()
                .anyMatch(existing -> overlaps(request.getStartTime(), request.getEndTime(), existing));

        if (hasConflict) {
            throw new ReservationConflictException();
        }

        // création de la réservation avec le statut CONFIRMED
        Reservation reservation = new Reservation();
        reservation.setRoomId(request.getRoomId());
        reservation.setBookedBy(request.getBookedBy());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setStatus(ReservationStatus.CONFIRMED);
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation getReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    @Override
    public Reservation cancelReservation(Long id) {
        Reservation reservation = getReservation(id);

        // on ne peut pas annuler deux fois la même réservation
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ReservationAlreadyCancelledException();
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservationRepository.save(reservation);
    }

    // vérifie si deux créneaux se chevauchent
    private boolean overlaps(LocalDateTime start, LocalDateTime end, Reservation existing) {
        return start.isBefore(existing.getEndTime()) && existing.getStartTime().isBefore(end);
    }
}
