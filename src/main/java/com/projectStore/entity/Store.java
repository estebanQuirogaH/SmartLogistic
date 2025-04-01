package com.projectStore.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ElementCollection
    @CollectionTable(name = "store_physical_stock", 
                    joinColumns = @JoinColumn(name = "store_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<String, Integer> physicalStock = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "store_virtual_stock", 
                    joinColumns = @JoinColumn(name = "store_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<String, Integer> virtualStock = new HashMap<>();

    @Transient // Este campo no se persistirá en la base de datos
    private Stock stock;

    // @Column(name = "virtual_stock_percentage") // Nombre de la columna en la base de datos
    // private Integer virtualStockPercentage;

    // @ManyToOne // Relación muchos a uno con User
    // @JoinColumn(name = "admin_id", nullable = false) // Llave foránea hacia la tabla de usuarios
    // private User admin;

    // Para mantener compatibilidad con código existente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", insertable = false, updatable = false)
    private User admin;

    // Método auxiliar para establecer el admin y el creatorId al mismo tiempo
    public void setAdmin(User admin) {
        this.admin = admin;
        if (admin != null) {
            this.creatorId = admin.getId();
        }
    }
}