package com.projectStore.entity;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audits") // Nombre de la tabla en la base de datos
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    private Long id;

    @Temporal(TemporalType.TIMESTAMP) // Indica que es un campo de tipo TIMESTAMP
    @Column(nullable = false, updatable = false) // No puede ser nulo y no se puede actualizar
    private Date timestamp;

    @Column(name = "ip_address", nullable = false, length = 45) // Longitud máxima para direcciones IP
    private String ipAddress;

    @Column(nullable = false, length = 255) // Descripción con longitud máxima de 255 caracteres
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) // Relación con la entidad User (carga perezosa)
    @JoinColumn(name = "user_id", nullable = false) // Llave foránea hacia la tabla de usuarios
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // Relación con la entidad Store (carga perezosa)
    @JoinColumn(name = "store_id", nullable = true) // Llave foránea hacia la tabla de tiendas (puede ser nula)
    private Store store;
}