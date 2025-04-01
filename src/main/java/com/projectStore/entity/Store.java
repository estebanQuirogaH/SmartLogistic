package com.projectStore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stores") // Nombre de la tabla en la base de datos
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    private Long id;

    @Column(nullable = false) // El nombre de la tienda no puede ser nulo
    private String name;

    @Embedded // Relación embebida con la clase Location
    private Location location;

    @Transient // Este campo no se persistirá en la base de datos
    private Stock stock;

    @Column(name = "virtual_stock_percentage") // Nombre de la columna en la base de datos
    private Integer virtualStockPercentage;

    @ManyToOne // Relación muchos a uno con User
    @JoinColumn(name = "admin_id") // Llave foránea hacia la tabla de usuarios
    private User admin;
}