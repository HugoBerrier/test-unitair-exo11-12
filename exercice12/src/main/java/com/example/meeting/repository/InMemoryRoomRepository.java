package com.example.meeting.repository;

import com.example.meeting.model.Room;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// Stockage des salles en mémoire (pas de base de données)
@Repository
public class InMemoryRoomRepository implements RoomRepository {

    private final Map<Long, Room> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Room save(Room room) {
        // génère un id automatiquement si la salle est nouvelle
        if (room.getId() == null) {
            room.setId(idGenerator.getAndIncrement());
        }
        storage.put(room.getId(), room);
        return room;
    }

    @Override
    public Optional<Room> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(storage.values());
    }

    // utilisé par les tests pour réinitialiser les données
    @Override
    public void clear() {
        storage.clear();
        idGenerator.set(1);
    }
}
