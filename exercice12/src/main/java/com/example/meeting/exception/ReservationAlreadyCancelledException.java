package com.example.meeting.exception;

public class ReservationAlreadyCancelledException extends RuntimeException {

    public ReservationAlreadyCancelledException() {
        super("Reservation is already cancelled");
    }
}
