package com.projectStore.service;

import org.springframework.stereotype.Service;

import com.projectStore.entity.Location;

@Service
public class LocationService {

    /**
     * Valida si una ubicación es válida según las reglas del negocio.
     * Por ejemplo, verifica si las coordenadas están dentro de un rango permitido.
     *
     * @param location La ubicación a validar.
     * @return true si la ubicación es válida, false en caso contrario.
     */
    public boolean isValidLocation(Location location) {
        // Ejemplo de validación: verifica que las coordenadas no sean nulas y estén dentro de un rango válido.
        if (location == null || location.getLatitude() == null || location.getLongitude() == null) {
            return false;
        }

        double latitude = Double.parseDouble(location.getLatitude());
        double longitude = Double.parseDouble(location.getLongitude());

        // Rango de latitud y longitud (puedes ajustar según tus necesidades)
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }

    /**
     * Calcula la distancia entre dos ubicaciones usando la fórmula de Haversine.
     *
     * @param location1 La primera ubicación.
     * @param location2 La segunda ubicación.
     * @return La distancia en kilómetros entre las dos ubicaciones.
     */
    public double calculateDistance(Location location1, Location location2) {
        if (location1 == null || location2 == null) {
            throw new IllegalArgumentException("Las ubicaciones no pueden ser nulas.");
        }

        double lat1 = Double.parseDouble(location1.getLatitude());
        double lon1 = Double.parseDouble(location1.getLongitude());
        double lat2 = Double.parseDouble(location2.getLatitude());
        double lon2 = Double.parseDouble(location2.getLongitude());

        final int R = 6371; // Radio de la Tierra en kilómetros
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
