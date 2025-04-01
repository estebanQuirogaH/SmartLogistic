package com.projectStore.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor  // Constructor sin argumentos (Lombok)
@AllArgsConstructor // Constructor con todos los campos (Lombok)
public class Location {
    
    private String address;    // Opcional (depende de tus requisitos)
    private double latitude;  
    private double longitude;

    
}