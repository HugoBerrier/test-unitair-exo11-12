package com.example.meeting.service;

import com.example.meeting.dto.CreateReservationRequest;
import com.example.meeting.exception.InvalidTimeSlotException;
import com.example.meeting.exception.ReservationAlreadyCancelledException;
import com.example.meeting.exception.ReservationConflictException;
import com.example.meeting.exception.RoomNotFoundException;
import com.example.meeting.model.Reservation;
import com.example.meeting.model.ReservationStatus;
import com.example.meeting.model.Room;
import com.example.meeting.repository.ReservationRepository;
import com.example.meeting.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 5, 22, 10, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 5, 22, 11, 0);

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private DefaultReservationService reservationService;

    @Test
    void shouldCreateValidReservation() {
        // Arrange
        CreateReservationRequest request = buildRequest(1L, "Alice", START, END);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(new Room(1L, "Salle A", 10)));
        when(reservationRepository.findConfirmedByRoomId(1L)).thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            reservation.setId(1L);
            return reservation;
        });

        // Act
        Reservation created = reservationService.createReservation(request);

        // Assert
        assertEquals(1L, created.getRoomId());
        assertEquals("Alice", created.getBookedBy());
        assertEquals(ReservationStatus.CONFIRMED, created.getStatus());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void shouldRejectWhenRoomDoesNotExist() {
        // Arrange
        CreateReservationRequest request = buildRequest(99L, "Alice", START, END);
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RoomNotFoundException.class, () -> reservationService.createReservation(request));
    }

    @Test
    void shouldRejectWhenTimeSlotIsInvalid() {
        // Arrange
        CreateReservationRequest request = buildRequest(1L, "Alice", END, START);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(new Room(1L, "Salle A", 10)));

        // Act & Assert
        assertThrows(InvalidTimeSlotException.class, () -> reservationService.createReservation(request));
    }

    @Test
    void shouldRejectWhenSlotOverlapsExistingReservation() {
        // Arrange
        CreateReservationRequest request = buildRequest(1L, "Bob", START, END);

        Reservation existing = new Reservation();
        existing.setId(1L);
        existing.setRoomId(1L);
        existing.setBookedBy("Alice");
        existing.setStartTime(START);
        existing.setEndTime(END);
        existing.setStatus(ReservationStatus.CONFIRMED);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(new Room(1L, "Salle A", 10)));
        when(reservationRepository.findConfirmedByRoomId(1L)).thenReturn(List.of(existing));

        // Act & Assert
        assertThrows(ReservationConflictException.class, () -> reservationService.createReservation(request));
    }

    @Test
    void shouldCancelConfirmedReservation() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setRoomId(1L);
        reservation.setBookedBy("Alice");
        reservation.setStartTime(START);
        reservation.setEndTime(END);
        reservation.setStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Reservation cancelled = reservationService.cancelReservation(1L);

        // Assert
        assertEquals(ReservationStatus.CANCELLED, cancelled.getStatus());
    }

    @Test
    void shouldRejectCancellationWhenAlreadyCancelled() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setRoomId(1L);
        reservation.setBookedBy("Alice");
        reservation.setStartTime(START);
        reservation.setEndTime(END);
        reservation.setStatus(ReservationStatus.CANCELLED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act & Assert
        assertThrows(ReservationAlreadyCancelledException.class,
                () -> reservationService.cancelReservation(1L));
    }

    private CreateReservationRequest buildRequest(Long roomId, String bookedBy,
                                                  LocalDateTime start, LocalDateTime end) {
        CreateReservationRequest request = new CreateReservationRequest();
        request.setRoomId(roomId);
        request.setBookedBy(bookedBy);
        request.setStartTime(start);
        request.setEndTime(end);
        return request;
    }
}
