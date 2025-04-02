package com.projectStore.config;

import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, Map.class, source -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(source, new TypeReference<Map<String, Integer>>() {});
            } catch (Exception e) {
                throw new RuntimeException("Error converting string to map", e);
            }
        });
    }
}