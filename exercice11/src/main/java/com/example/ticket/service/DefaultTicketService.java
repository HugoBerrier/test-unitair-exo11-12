package com.example.ticket.service;

import com.example.ticket.dto.CreateTicketRequest;
import com.example.ticket.exception.InvalidStatusTransitionException;
import com.example.ticket.exception.TicketNotFoundException;
import com.example.ticket.model.Ticket;
import com.example.ticket.model.TicketStatus;
import com.example.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultTicketService implements TicketService {

    private final TicketRepository ticketRepository;

    public DefaultTicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Ticket create(CreateTicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setPriority(request.getPriority());
        ticket.setStatus(TicketStatus.OPEN);  // un nouveau ticket est toujours OPEN
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket getById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    @Override
    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket updateStatus(Long id, TicketStatus newStatus) {
        Ticket ticket = getById(id);
        validateTransition(ticket.getStatus(), newStatus);
        ticket.setStatus(newStatus);
        return ticketRepository.save(ticket);
    }

    // vérifie que le changement de statut est autorisé
    private void validateTransition(TicketStatus current, TicketStatus target) {
        if (current == TicketStatus.RESOLVED) {
            throw new InvalidStatusTransitionException(current, target);
        }
        if (current == TicketStatus.OPEN) {
            if (target != TicketStatus.IN_PROGRESS && target != TicketStatus.RESOLVED) {
                throw new InvalidStatusTransitionException(current, target);
            }
        } else if (current == TicketStatus.IN_PROGRESS) {
            if (target != TicketStatus.RESOLVED) {
                throw new InvalidStatusTransitionException(current, target);
            }
        }
    }
}
