package com.projectStore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectStore.entity.Location;
import com.projectStore.entity.Store;
import com.projectStore.entity.User;
import com.projectStore.repository.StoreRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final ParameterService parameterService;
    private final AuditService auditService;

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public Store getStoreById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tienda no encontrada: " + id));
    }

    public List<Store> getStoresByAdmin(User admin) {
        return storeRepository.findByAdmin(admin);
    }

    @Transactional
    public Store createStore(Store store, String ipAddress, User creator) {
        // Validar distancia mínima con otras tiendas
        if (!isLocationValid(store.getLocation())) {
            throw new RuntimeException("La ubicación de la tienda está demasiado cerca de otra tienda existente.");
        }
        // Establecer el porcentaje de stock virtual
        store.setVirtualStockPercentage(parameterService.getVirtualStockPercentage());
        Store savedStore = storeRepository.save(store);
        // Registrar auditoría
        auditService.registerAudit(
                ipAddress,
                "Creación de tienda: " + store.getName(),
                creator,
                savedStore);
        return savedStore;
    }

    @Transactional
    public Store updateStore(Store store) {
        return storeRepository.save(store);
    }

    @Transactional
    public void deleteStore(Long id, String ipAddress, User user) {
        Store store = getStoreById(id);
        storeRepository.delete(store);
        // Registrar auditoría
        auditService.registerAudit(
                ipAddress,
                "Eliminación de tienda: " + store.getName(),
                user,
                null);
    }

    private boolean isLocationValid(Location newLocation) {
        double minDistance = parameterService.getMinimumDistanceBetweenStores();
        List<Store> existingStores = storeRepository.findAll();
        for (Store store : existingStores) {
            Location existingLocation = store.getLocation();
            double distance = calculateDistance(
                    Double.parseDouble(newLocation.getLatitude()),
                    Double.parseDouble(newLocation.getLongitude()),
                    Double.parseDouble(existingLocation.getLatitude()),
                    Double.parseDouble(existingLocation.getLongitude()));
            if (distance < minDistance) {
                return false;
            }
        }
        return true;
    }

    // Cálculo de distancia entre dos puntos usando la fórmula de Haversine
    private double calculateDistance(double lat1, double lon1, double lat2,
            double lon2) {
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
