package com.example.meeting.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// Données envoyées pour créer une salle
public class CreateRoomRequest {

    @NotBlank  // le nom est obligatoire
    private String name;

    @Min(1)  // la capacité doit être au moins 1
    private int capacity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
