package com.example.ticket.repository;

import com.example.ticket.model.Ticket;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// Stockage des tickets en mémoire (pas de base de données)
@Repository
public class InMemoryTicketRepository implements TicketRepository {

    private final Map<Long, Ticket> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Ticket save(Ticket ticket) {
        // génère un id automatiquement si le ticket est nouveau
        if (ticket.getId() == null) {
            ticket.setId(idGenerator.getAndIncrement());
        }
        storage.put(ticket.getId(), ticket);
        return ticket;
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Ticket> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void clear() {
        storage.clear();
        idGenerator.set(1);
    }
}
