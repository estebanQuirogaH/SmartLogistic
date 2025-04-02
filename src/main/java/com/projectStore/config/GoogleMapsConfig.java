package com.projectStore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleMapsConfig {
    
    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    @Bean
    public String googleMapsApiKey() {
        return googleMapsApiKey;
    }
}