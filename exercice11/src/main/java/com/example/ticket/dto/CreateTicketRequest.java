package com.example.ticket.dto;

import com.example.ticket.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Données envoyées pour créer un ticket
public class CreateTicketRequest {

    @NotBlank
    @Size(min = 3)  // le titre doit avoir au moins 3 caractères
    private String title;

    @NotNull  // la priorité est obligatoire
    private Priority priority;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
