package com.projectStore.service;

import com.projectStore.entity.Location;
import com.projectStore.entity.Parameter;
import com.projectStore.entity.Store;
import com.projectStore.repository.ParameterRepository;
import com.projectStore.repository.StoreRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private ParameterRepository parameterRepository;
    
    @Autowired
    private AuditService auditService;

    /**
     * Calcular la distancia entre dos puntos usando la fórmula de Haversine
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
     * @param location ubicación propuesta para la nueva tienda
     * @return true si es válida, false si no
     */
    public boolean isValidStoreLocation(Location location) {
        // Obtener el parámetro de distancia mínima entre tiendas
        Parameter distanceParam = parameterRepository.findByName("MIN_DISTANCE_BETWEEN_STORES");
        if (distanceParam == null) {
            // Si no existe el parámetro, usar un valor predeterminado de 1 km
            return validateMinimumDistance(location, 1.0);
        }
        
        try {
            double minDistance = Double.parseDouble(distanceParam.getValue());
            return validateMinimumDistance(location, minDistance);
        } catch (NumberFormatException e) {
            // Si el valor no es un número válido, usar un valor predeterminado
            return validateMinimumDistance(location, 1.0);
        }
    }

    /**
     * Valida la distancia mínima entre la ubicación propuesta y las tiendas existentes
     * @param location ubicación propuesta
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
                    storeLocation.getLatitude(), storeLocation.getLongitude()
                );
                
                if (distance < minDistance) {
                    // Registrar intento fallido en la auditoría
                    auditService.logEvent(
                        "LOCATION_VALIDATION_FAILED",
                        String.format("Tienda demasiado cerca (%.2f km) de tienda existente ID=%d", 
                                     distance, store.getId())
                    );
                    return false;
                }
            }
        }
        
        return true;
    }
}