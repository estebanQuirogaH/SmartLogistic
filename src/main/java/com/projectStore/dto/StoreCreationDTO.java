package com.projectStore.dto;

import java.util.Map;

public class StoreCreationDTO {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Map<String, Integer> initialStock; // Producto ID -> Cantidad

    public StoreCreationDTO() {
    }

    public StoreCreationDTO(String name, String address, Double latitude, Double longitude, Map<String, Integer> initialStock) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.initialStock = initialStock;
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

    public Map<String, Integer> getInitialStock() {
        return initialStock;
    }

    public void setInitialStock(Map<String, Integer> initialStock) {
        this.initialStock = initialStock;
    }
}