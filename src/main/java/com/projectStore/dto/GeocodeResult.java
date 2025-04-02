package com.projectStore.dto;

import java.util.List;

import lombok.Data;

@Data
public class GeocodeResult {
    private Geometry geometry;
    private String formatted_address;
}