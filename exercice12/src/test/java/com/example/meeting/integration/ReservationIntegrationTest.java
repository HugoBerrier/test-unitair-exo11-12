package com.example.meeting.integration;

import com.example.meeting.repository.InMemoryReservationRepository;
import com.example.meeting.repository.InMemoryRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryRoomRepository roomRepository;

    @Autowired
    private InMemoryReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        roomRepository.clear();
        reservationRepository.clear();
    }

    @Test
    void shouldCreateRoomReservationGetAndCancelThroughApi() throws Exception {
        // 1. créer une salle
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Salle A","capacity":10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        // 2. créer une réservation
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"roomId":1,"bookedBy":"Alice","startTime":"2026-05-22T10:00:00","endTime":"2026-05-22T11:00:00"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        // 3. consulter la réservation
        mockMvc.perform(get("/api/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookedBy").value("Alice"));

        // 4. annuler la réservation
        mockMvc.perform(patch("/api/reservations/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
