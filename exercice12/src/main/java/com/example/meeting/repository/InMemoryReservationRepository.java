package com.example.meeting.repository;

import com.example.meeting.model.Reservation;
import com.example.meeting.model.ReservationStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryReservationRepository implements ReservationRepository {

    private final Map<Long, Reservation> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Reservation save(Reservation reservation) {
        if (reservation.getId() == null) {
            reservation.setId(idGenerator.getAndIncrement());
        }
        storage.put(reservation.getId(), reservation);
        return reservation;
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Reservation> findConfirmedByRoomId(Long roomId) {
        return storage.values().stream()
                .filter(r -> roomId.equals(r.getRoomId()))
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .toList();
    }
}
