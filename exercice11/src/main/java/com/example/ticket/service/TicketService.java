package com.example.ticket.service;

import com.example.ticket.dto.CreateTicketRequest;
import com.example.ticket.model.Ticket;
import com.example.ticket.model.TicketStatus;

import java.util.List;

public interface TicketService {

    Ticket create(CreateTicketRequest request);

    Ticket getById(Long id);

    List<Ticket> getAll();

    Ticket updateStatus(Long id, TicketStatus newStatus);
}
