package com.example.meeting.controller;

import com.example.meeting.exception.GlobalExceptionHandler;
import com.example.meeting.exception.ReservationConflictException;
import com.example.meeting.exception.ReservationNotFoundException;
import com.example.meeting.model.Reservation;
import com.example.meeting.model.ReservationStatus;
import com.example.meeting.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 5, 22, 10, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 5, 22, 11, 0);

    @Mock
    private ReservationService reservationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ReservationController controller = new ReservationController(reservationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturn201WhenCreatingValidReservation() throws Exception {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setRoomId(1L);
        reservation.setBookedBy("Alice");
        reservation.setStartTime(START);
        reservation.setEndTime(END);
        reservation.setStatus(ReservationStatus.CONFIRMED);

        when(reservationService.createReservation(any())).thenReturn(reservation);

        // Act & Assert
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"roomId":1,"bookedBy":"Alice","startTime":"2026-05-22T10:00:00","endTime":"2026-05-22T11:00:00"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void shouldReturn404WhenReservationNotFound() throws Exception {
        // Arrange
        when(reservationService.getReservation(99L)).thenThrow(new ReservationNotFoundException(99L));

        // Act & Assert
        mockMvc.perform(get("/api/reservations/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Reservation not found with id: 99"));
    }

    @Test
    void shouldReturn409WhenReservationConflictOccurs() throws Exception {
        // Arrange
        when(reservationService.createReservation(any())).thenThrow(new ReservationConflictException());

        // Act & Assert
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"roomId":1,"bookedBy":"Bob","startTime":"2026-05-22T10:00:00","endTime":"2026-05-22T11:00:00"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Reservation conflicts with an existing reservation"));
    }
}
