package com.example.ticket.controller;

import com.example.ticket.dto.CreateTicketRequest;
import com.example.ticket.dto.TicketResponse;
import com.example.ticket.dto.UpdateStatusRequest;
import com.example.ticket.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TicketResponse.from(ticketService.create(request)));
    }

    @GetMapping
    public List<TicketResponse> getAll() {
        return ticketService.getAll().stream()
                .map(TicketResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public TicketResponse getById(@PathVariable Long id) {
        return TicketResponse.from(ticketService.getById(id));
    }

    @PatchMapping("/{id}/status")
    public TicketResponse updateStatus(@PathVariable Long id,
                                       @Valid @RequestBody UpdateStatusRequest request) {
        return TicketResponse.from(ticketService.updateStatus(id, request.getStatus()));
    }
}
