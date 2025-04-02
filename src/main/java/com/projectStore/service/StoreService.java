package com.projectStore.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.projectStore.dto.StoreCreationDTO;
import com.projectStore.dto.StoreDTO;
import com.projectStore.entity.Location;
import com.projectStore.entity.Parameter;
import com.projectStore.entity.Store;
import com.projectStore.entity.User;
import com.projectStore.mapper.StoreMapper;
import com.projectStore.repository.ParameterRepository;
import com.projectStore.repository.StoreRepository;
import com.projectStore.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StoreService {

    // private final StoreRepository storeRepository;
    // private final ParameterService parameterService;
    // private final AuditService auditService;
    // private final UserRepository userRepository;

    private final StoreRepository storeRepository;
    private final ParameterRepository parameterRepository;
    private final UserRepository userRepository;
    private final StoreMapper storeMapper;
    private final LocationService locationService;
    private final AuditService auditService;
    private final ParameterService parameterService;

    // obtener todas las tiendas
    // public List<Store> getAllStores() {
    // return storeRepository.findAll();
    // }
    public List<StoreDTO> getAllStores() {
        List<Store> stores = storeRepository.findAll();
        return storeMapper.toDTOList(stores);
    }

    public Store findStoreEntityById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tienda no encontrada"));
    }

    /**
     * Obtiene una tienda por su ID
     */
    public StoreDTO getStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tienda no encontrada"));
        return storeMapper.toDTO(store);
    }

    public List<Store> getStoresByAdmin(User admin) {
        return storeRepository.findByAdmin(admin);
    }

    // @Transactional
    // public Store createStore(Store store, String ipAddress, User creator) {
    // // Validar distancia mínima con otras tiendas
    // if (!isLocationValid(store.getLocation())) {
    // throw new RuntimeException("La ubicación de la tienda está demasiado cerca de
    // otra tienda existente.");
    // }
    // // Establecer el porcentaje de stock virtual
    // store.setVirtualStockPercentage(parameterService.getVirtualStockPercentage());
    // Store savedStore = storeRepository.save(store);
    // // Registrar auditoría
    // auditService.registerAudit(
    // ipAddress,
    // "Creación de tienda: " + store.getName(),
    // creator,
    // savedStore);
    // return savedStore;
    // }

    /**
     * Crea una nueva tienda
     */

    @Transactional
    public StoreDTO createStore(StoreCreationDTO storeDTO, Long adminId) {
        // Verificar que el usuario admin existe
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario admin no válido"));

        // Obtener coordenadas desde la dirección si no se provee latitud/longitud
        Location location = (storeDTO.getLatitude() != null && storeDTO.getLongitude() != null)
                ? new Location(storeDTO.getAddress(), storeDTO.getLatitude(), storeDTO.getLongitude())
                : locationService.getCoordinatesFromAddress(storeDTO.getAddress());

        if (location == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se pudo obtener coordenadas para la dirección");
        }

        // Validar la ubicación
        if (!locationService.isValidStoreLocation(location)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La ubicación no cumple con la distancia mínima requerida entre tiendas");
        }

        // Crear la tienda usando el mapper y asignar la ubicación
        Store store = storeMapper.toEntity(storeDTO);
        store.setLocation(location);

        // Asignar el mismo admin que crea la tienda como administrador
        store.setAdmin(admin);

        // Calcular stock virtual
        calculateVirtualStock(store);

        // Guardar la tienda
        store = storeRepository.save(store);

        // Registrar auditoría
        auditService.registerAudit(
                store.getId().toString(),
                "Creación de tienda: " + store.getName(),
                admin, store);

        return storeMapper.toDTO(store);
    }

    /**
     * Actualiza una tienda existente
     */
    @Transactional
    public Store updateStore(StoreCreationDTO storeDTO) {

        Store existingStore = storeRepository.findById(storeDTO.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tienda no encontrada"));

        // Si la ubicación cambió, obtener nuevas coordenadas y validar
        if (!existingStore.getLocation().getAddress().equals(storeDTO.getAddress())) {
            Location newLocation = locationService.getCoordinatesFromAddress(storeDTO.getAddress());
            if (newLocation == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No se pudo obtener coordenadas para la dirección");
            }
            if (!locationService.isValidStoreLocation(newLocation)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La nueva ubicación no cumple con la distancia mínima requerida entre tiendas");
            }
            existingStore.setLocation(newLocation);
        }
        // Actualizar la tienda
        storeMapper.updateEntityFromDTO(existingStore, storeDTO);
        calculateVirtualStock(existingStore);
        existingStore = storeRepository.save(existingStore);
        // Registrar auditoría
        auditService.registerAudit(
                storeDTO.getId().toString(),
                "Actualizacion de tienda: " + existingStore.getName(),
                existingStore.getAdmin(), existingStore);

        return existingStore;
    }

    /**
     * Elimina una tienda existente
     */
    @Transactional
    public void deleteStore(Long id, Long adminId) {
        // Verificar que la tienda existe
        Store existingStore = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tienda no encontrada"));

        // Eliminar la tienda
        storeRepository.delete(existingStore);

        // Registrar auditoría de eliminación
        auditService.registerAudit(
                id.toString(),
                "Eliminación de tienda: " + existingStore.getName(),
                existingStore.getAdmin(),
                existingStore);
    }

    /**
     * Calcula el stock virtual basado en el stock físico y el porcentaje
     * configurado
     */
    private void calculateVirtualStock(Store store) {

        // Calcular el stock virtual para cada producto
        Map<String, Integer> virtualStock = new HashMap<>();
        for (Map.Entry<String, Integer> entry : store.getPhysicalStock().entrySet()) {
            int physicalAmount = entry.getValue();
            int virtualAmount = (int) Math
                    .floor(physicalAmount * (parameterService.getVirtualStockPercentage() / 100.0));
            virtualStock.put(entry.getKey(), virtualAmount);
        }

        store.setVirtualStock(virtualStock);
    }

    public Store getStoreEntityById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tienda no encontrada"));
    }

    @Transactional
    public Store updateStoreEntity(Store store) {
        return storeRepository.save(store);
    }

}
