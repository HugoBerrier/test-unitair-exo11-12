package com.example.ticket.dto;

import com.example.ticket.model.Priority;
import com.example.ticket.model.Ticket;
import com.example.ticket.model.TicketStatus;

public class TicketResponse {

    private final Long id;
    private final String title;
    private final Priority priority;
    private final TicketStatus status;

    public TicketResponse(Long id, String title, Priority priority, TicketStatus status) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.status = status;
    }

    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getPriority(),
                ticket.getStatus()
        );
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Priority getPriority() {
        return priority;
    }

    public TicketStatus getStatus() {
        return status;
    }
}
