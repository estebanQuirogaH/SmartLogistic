package com.projectStore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectStore.entity.Location;
import com.projectStore.entity.Parameter;
import com.projectStore.entity.Store;
import com.projectStore.repository.ParameterRepository;
import com.projectStore.repository.StoreRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ParameterRepository parameterRepository;

    @Autowired
    private AuditService auditService;

    private final String apiKey;

    public LocationService() {
        Dotenv dotenv = Dotenv.load(); // Cargar variables de entorno desde .env
        this.apiKey = dotenv.get("GOOGLE_API_KEY");
    }

    /**
     * Calcular la distancia entre dos puntos usando la fórmula de Haversine
     * 
     * @param lat1 latitud del primer punto
     * @param lon1 longitud del primer punto
     * @param lat2 latitud del segundo punto
     * @param lon2 longitud del segundo punto
     * @return distancia en kilómetros
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Radio de la Tierra en kilómetros
        final int R = 6371;

        // Convertir a radianes
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        // Fórmula de Haversine
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distancia en kilómetros
        return R * c;
    }

    /**
     * Valida si es posible crear una tienda en la ubicación dada
     * según la distancia mínima establecida por el superadmin
     * 
     * @param location ubicación propuesta para la nueva tienda
     * @return true si es válida, false si no
     */
    public boolean isValidStoreLocation(Location location) {
        double minDistance = parameterRepository.findByName("MIN_DISTANCE_BETWEEN_STORES")
                .map(param -> {
                    try {
                        return Double.parseDouble(param.getValue());
                    } catch (NumberFormatException e) {
                        return 1.0; // Valor por defecto si hay error de formato
                    }
                })
                .orElse(1.0); // Valor por defecto si no existe el parámetro

        return validateMinimumDistance(location, minDistance);
    }

    /**
     * Valida la distancia mínima entre la ubicación propuesta y las tiendas
     * existentes
     * 
     * @param location    ubicación propuesta
     * @param minDistance distancia mínima requerida en kilómetros
     * @return true si cumple con la distancia mínima, false si no
     */
    private boolean validateMinimumDistance(Location location, double minDistance) {
        List<Store> existingStores = storeRepository.findAll();

        for (Store store : existingStores) {
            Location storeLocation = store.getLocation();
            if (storeLocation != null) {
                double distance = calculateDistance(
                        location.getLatitude(), location.getLongitude(),
                        storeLocation.getLatitude(), storeLocation.getLongitude());

                if (distance < minDistance) {
                    // // Registrar intento fallido en la auditoría
                    // auditService.logEvent(
                    // "LOCATION_VALIDATION_FAILED",
                    // String.format("Tienda demasiado cerca (%.2f km) de tienda existente ID=%d",
                    // distance, store.getId())
                    // );
                    return false;
                }
            }
        }

        return true;
    }

    public Location getCoordinatesFromAddress(String inputAddress) {
        try {
            String encodedAddress = URLEncoder.encode(inputAddress, StandardCharsets.UTF_8);
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encodedAddress + "&key=" + apiKey;

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            if (root.has("results") && root.get("results").size() > 0) {
                JsonNode firstResult = root.get("results").get(0);
                String formattedAddress = firstResult.get("formatted_address").asText();

                JsonNode locationNode = firstResult.get("geometry").get("location");
                double lat = locationNode.get("lat").asDouble();
                double lng = locationNode.get("lng").asDouble();

                return new Location(formattedAddress, lat, lng);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Para ver errores en la consola
        }
        return null;
    }
}