package com.projectStore.dto;

import java.util.List;

import lombok.Data;

@Data
public class GeocodeResponse {
    private List<GeocodeResult> results;
    private String status;
}