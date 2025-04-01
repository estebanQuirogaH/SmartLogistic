package com.projectStore.entity;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parameters") // Nombre de la tabla en la base de datos
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Parameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    private Long id;

    @Column(nullable = false, unique = true) // El nombre debe ser único y no nulo
    private String name;

    @Column(nullable = false) // El valor no puede ser nulo
    private String value;

    @Column(nullable = false) // Descripción con longitud máxima de 255 caracteres
    private String description;

    @Temporal(TemporalType.TIMESTAMP) // Indica que es un campo de tipo TIMESTAMP
    @Column(name = "last_modified") // Nombre de la columna en la base de datos
    private Date lastModified;

    @ManyToOne // Relación muchos a uno con User
    @JoinColumn(name = "modified_by") // Llave foránea hacia la tabla de usuarios
    private User modifiedBy;
}