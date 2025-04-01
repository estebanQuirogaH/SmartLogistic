package com.projectStore.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa una ubicación con latitud y longitud.
 * Es utilizada como un componente embebido en otras entidades.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private String id;
    private String Description;
    private String latitude; // Latitud de la ubicación
    private String longitude; // Longitud de la ubicación    
}