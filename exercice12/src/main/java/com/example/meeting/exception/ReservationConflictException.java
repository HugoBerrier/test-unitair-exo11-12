package com.example.meeting.exception;

public class ReservationConflictException extends RuntimeException {

    public ReservationConflictException() {
        super("Reservation conflicts with an existing reservation");
    }
}
