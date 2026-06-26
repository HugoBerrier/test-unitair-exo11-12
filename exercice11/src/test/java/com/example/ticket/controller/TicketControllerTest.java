package com.example.ticket.controller;

import com.example.ticket.exception.GlobalExceptionHandler;
import com.example.ticket.exception.InvalidStatusTransitionException;
import com.example.ticket.exception.TicketNotFoundException;
import com.example.ticket.model.Priority;
import com.example.ticket.model.Ticket;
import com.example.ticket.model.TicketStatus;
import com.example.ticket.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        TicketController controller = new TicketController(ticketService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new LocalValidatorFactoryBean())
                .build();
    }

    @Test
    void shouldReturn201WhenCreatingValidTicket() throws Exception {
        // Arrange
        Ticket ticket = buildTicket(1L, "Server down", Priority.HIGH, TicketStatus.OPEN);
        when(ticketService.create(any())).thenReturn(ticket);

        // Act & Assert
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Server down","priority":"HIGH"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Server down"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"ab","priority":"HIGH"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturn200WhenGettingExistingTicket() throws Exception {
        // Arrange
        Ticket ticket = buildTicket(1L, "Bug", Priority.LOW, TicketStatus.OPEN);
        when(ticketService.getById(1L)).thenReturn(ticket);

        // Act & Assert
        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Bug"));
    }

    @Test
    void shouldReturn404WhenTicketNotFound() throws Exception {
        // Arrange
        when(ticketService.getById(99L)).thenThrow(new TicketNotFoundException(99L));

        // Act & Assert
        mockMvc.perform(get("/api/tickets/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ticket not found with id: 99"));
    }

    @Test
    void shouldReturn409WhenStatusTransitionIsForbidden() throws Exception {
        // Arrange
        when(ticketService.updateStatus(eq(1L), eq(TicketStatus.IN_PROGRESS)))
                .thenThrow(new InvalidStatusTransitionException(TicketStatus.RESOLVED, TicketStatus.IN_PROGRESS));

        // Act & Assert
        mockMvc.perform(patch("/api/tickets/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"IN_PROGRESS"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn200WhenListingTickets() throws Exception {
        // Arrange
        when(ticketService.getAll()).thenReturn(List.of(
                buildTicket(1L, "Bug", Priority.LOW, TicketStatus.OPEN)
        ));

        // Act & Assert
        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Bug"));
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
