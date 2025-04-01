package com.projectStore.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.projectStore.entity.Location;
import com.projectStore.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreDTO {
    private Long id;
    private String name;
    private Location location;
    private String address;
    private Double latitude;
    private Double longitude;
    private String creatorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Integer> physicalStock; // Producto ID -> Cantidad física
    private Map<String, Integer> virtualStock; // Producto ID -> Cantidad virtual
    private int virtual_stock_percentage;
    private User admin;

}