package com.example.ticket.exception;

import com.example.ticket.model.TicketStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(TicketStatus current, TicketStatus target) {
        super("Invalid status transition from " + current + " to " + target);
    }
}
