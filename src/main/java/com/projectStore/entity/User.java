package com.projectStore.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users") // Nombre de la tabla en la base de datos
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    private Long id;

    @Column(unique = true, nullable = false) // El correo debe ser único y no nulo
    private String email;

    @Column(nullable = false) // La contraseña no puede ser nula
    private String password;

    @ManyToMany(fetch = FetchType.EAGER) // Relación muchos a muchos con roles
    @JoinTable(name = "user_roles", // Nombre de la tabla intermedia
            joinColumns = @JoinColumn(name = "user_id"), // Llave foránea hacia User
            inverseJoinColumns = @JoinColumn(name = "role_id") // Llave foránea hacia Role
    )
    private List<RoleEntity> roles;
}
