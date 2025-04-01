package com.projectStore.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Service
public class GeolocationService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    private final String GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    public String getCoordinates(String address) {
        String url = String.format("%s?address=%s&key=%s", GEOCODING_URL, address.replace(" ", "+"), apiKey);
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        return response;
    }
}
