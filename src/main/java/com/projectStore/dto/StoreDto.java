package com.projectStore.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class StoreDTO {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Integer> physicalStock; // Producto ID -> Cantidad física
    private Map<String, Integer> virtualStock; // Producto ID -> Cantidad virtual

    public StoreDTO() {
    }

    public StoreDTO(Long id, String name, String address, Double latitude, Double longitude, 
                   Long creatorId, String creatorName, LocalDateTime createdAt, 
                   LocalDateTime updatedAt, Map<String, Integer> physicalStock, 
                   Map<String, Integer> virtualStock) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.physicalStock = physicalStock;
        this.virtualStock = virtualStock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Map<String, Integer> getPhysicalStock() {
        return physicalStock;
    }

    public void setPhysicalStock(Map<String, Integer> physicalStock) {
        this.physicalStock = physicalStock;
    }

    public Map<String, Integer> getVirtualStock() {
        return virtualStock;
    }

    public void setVirtualStock(Map<String, Integer> virtualStock) {
        this.virtualStock = virtualStock;
    }
}