package com.projectStore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles") // Nombre de la tabla en la base de datos
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity implements Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    private Long id;

    @Column(unique = true, nullable = false) // El nombre del rol debe ser único y no nulo
    private String name;

}