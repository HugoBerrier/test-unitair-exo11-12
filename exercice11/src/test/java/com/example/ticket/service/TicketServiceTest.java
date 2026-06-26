package com.example.ticket.service;

import com.example.ticket.dto.CreateTicketRequest;
import com.example.ticket.exception.InvalidStatusTransitionException;
import com.example.ticket.exception.TicketNotFoundException;
import com.example.ticket.model.Priority;
import com.example.ticket.model.Ticket;
import com.example.ticket.model.TicketStatus;
import com.example.ticket.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private DefaultTicketService ticketService;

    @Test
    void shouldCreateTicketWithCorrectData() {
        // Arrange
        CreateTicketRequest request = new CreateTicketRequest();
        request.setTitle("Server down");
        request.setPriority(Priority.HIGH);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket ticket = invocation.getArgument(0);
            ticket.setId(1L);
            return ticket;
        });

        // Act
        Ticket created = ticketService.create(request);

        // Assert
        assertEquals("Server down", created.getTitle());
        assertEquals(Priority.HIGH, created.getPriority());
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void shouldCreateTicketWithOpenStatusByDefault() {
        // Arrange
        CreateTicketRequest request = new CreateTicketRequest();
        request.setTitle("Bug report");
        request.setPriority(Priority.LOW);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Ticket created = ticketService.create(request);

        // Assert
        assertEquals(TicketStatus.OPEN, created.getStatus());
    }

    @Test
    void shouldReturnTicketWhenItExists() {
        // Arrange
        Ticket ticket = buildTicket(1L, "Issue", Priority.MEDIUM, TicketStatus.OPEN);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        // Act
        Ticket found = ticketService.getById(1L);

        // Assert
        assertEquals(1L, found.getId());
        assertEquals("Issue", found.getTitle());
    }

    @Test
    void shouldThrowWhenTicketDoesNotExist() {
        // Arrange
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TicketNotFoundException.class, () -> ticketService.getById(99L));
    }

    @Test
    void shouldAllowOpenToInProgressTransition() {
        // Arrange
        Ticket ticket = buildTicket(1L, "Issue", Priority.MEDIUM, TicketStatus.OPEN);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Ticket updated = ticketService.updateStatus(1L, TicketStatus.IN_PROGRESS);

        // Assert
        assertEquals(TicketStatus.IN_PROGRESS, updated.getStatus());
    }

    @Test
    void shouldAllowOpenToResolvedTransition() {
        // Arrange
        Ticket ticket = buildTicket(1L, "Issue", Priority.MEDIUM, TicketStatus.OPEN);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Ticket updated = ticketService.updateStatus(1L, TicketStatus.RESOLVED);

        // Assert
        assertEquals(TicketStatus.RESOLVED, updated.getStatus());
    }

    @Test
    void shouldAllowInProgressToResolvedTransition() {
        // Arrange
        Ticket ticket = buildTicket(1L, "Issue", Priority.MEDIUM, TicketStatus.IN_PROGRESS);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Ticket updated = ticketService.updateStatus(1L, TicketStatus.RESOLVED);

        // Assert
        assertEquals(TicketStatus.RESOLVED, updated.getStatus());
    }

    @Test
    void shouldRejectTransitionFromResolvedStatus() {
        // Arrange
        Ticket ticket = buildTicket(1L, "Issue", Priority.MEDIUM, TicketStatus.RESOLVED);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        // Act & Assert
        assertThrows(InvalidStatusTransitionException.class,
                () -> ticketService.updateStatus(1L, TicketStatus.IN_PROGRESS));
    }

    private Ticket buildTicket(Long id, String title, Priority priority, TicketStatus status) {
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setTitle(title);
        ticket.setPriority(priority);
        ticket.setStatus(status);
        return ticket;
    }
}
