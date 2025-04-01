package com.projectStore.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreCreationDTO {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Map<String, Integer> initialStock; // Producto ID -> Cantidad

}