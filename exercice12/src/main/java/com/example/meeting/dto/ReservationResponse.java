package com.example.meeting.dto;

import com.example.meeting.model.Reservation;

public class ReservationResponse {

    private final Long id;
    private final Long roomId;
    private final String bookedBy;
    private final String startTime;
    private final String endTime;
    private final String status;

    public ReservationResponse(Long id, Long roomId, String bookedBy,
                               String startTime, String endTime, String status) {
        this.id = id;
        this.roomId = roomId;
        this.bookedBy = bookedBy;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getRoomId(),
                reservation.getBookedBy(),
                reservation.getStartTime().toString(),
                reservation.getEndTime().toString(),
                reservation.getStatus().name()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }
}
