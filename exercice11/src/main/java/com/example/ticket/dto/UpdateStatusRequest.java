package com.example.ticket.dto;

import com.example.ticket.model.TicketStatus;
import jakarta.validation.constraints.NotNull;

// Données envoyées pour modifier le statut d'un ticket
public class UpdateStatusRequest {

    @NotNull
    private TicketStatus status;

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
