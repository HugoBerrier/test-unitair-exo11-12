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
        roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(request.getRoomId()));

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new InvalidTimeSlotException();
        }

        boolean hasConflict = reservationRepository.findConfirmedByRoomId(request.getRoomId()).stream()
                .anyMatch(existing -> overlaps(request.getStartTime(), request.getEndTime(), existing));

        if (hasConflict) {
            throw new ReservationConflictException();
        }

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
