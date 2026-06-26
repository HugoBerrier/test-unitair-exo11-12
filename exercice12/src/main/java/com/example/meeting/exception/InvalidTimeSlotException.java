package com.example.meeting.exception;

public class InvalidTimeSlotException extends RuntimeException {

    public InvalidTimeSlotException() {
        super("End time must be after start time");
    }
}
