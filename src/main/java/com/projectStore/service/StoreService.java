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

    private final StoreRepository storeRepository;
    private final ParameterRepository parameterRepository;
    private final UserRepository userRepository;
    private final StoreMapper storeMapper;
    private final LocationService locationService;
    private final AuditService auditService;
    private final ParameterService parameterService;

    /**
     * Obtener todas las tiendas como DTOs
     */
    public List<StoreDTO> getAllStores() {
        List<Store> stores = storeRepository.findAll();
        return storeMapper.toDTOList(stores);
    }

    /**
     * Buscar entidad Store por ID
     */
    public Store findStoreEntityById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Tienda no encontrada"));
    }

    /**
     * Obtiene una tienda por su ID como DTO
     */
    public StoreDTO getStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tienda no encontrada"));
        return storeMapper.toDTO(store);
    }

    /**
     * Obtiene la entidad Store por su ID
     */
    public Store getStoreEntityById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tienda no encontrada"));
    }

    /**
     * Obtiene todas las tiendas administradas por un administrador específico
     */
    public List<Store> getStoresByAdmin(User admin) {
        return storeRepository.findByAdmin(admin);
    }

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

        // Asignar el admin que crea la tienda
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
                "Actualización de tienda: " + existingStore.getName(),
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

        // Verificar que el admin que solicita la eliminación es el mismo que la administra
        if (!existingStore.getAdmin().getId().equals(adminId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "No tienes permisos para eliminar esta tienda");
        }

        // Eliminar la tienda
        storeRepository.delete(existingStore);

        // Registrar auditoría de eliminación
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario admin no válido"));
        
        auditService.registerAudit(
                id.toString(),
                "Eliminación de tienda: " + existingStore.getName(),
                admin,
                existingStore);
    }

    /**
     * Calcular el stock virtual basado en el stock físico y el porcentaje configurado
     */
    private void calculateVirtualStock(Store store) {
        // Calcular el stock virtual para cada producto
        Map<String, Integer> virtualStock = new HashMap<>();
        for (Map.Entry<String, Integer> entry : store.getPhysicalStock().entrySet()) {
            int physicalAmount = entry.getValue();
            int virtualAmount = (int) Math.floor(physicalAmount * (parameterService.getVirtualStockPercentage() / 100.0));
            virtualStock.put(entry.getKey(), virtualAmount);
        }
        store.setVirtualStock(virtualStock);
    }

    /**
     * Actualiza una entidad Store
     */
    @Transactional
    public Store updateStoreEntity(Store store) {
        return storeRepository.save(store);
    }
    
    /**
     * Convierte una entidad Store a StoreCreationDTO para edición
     */
    public StoreCreationDTO convertToCreationDTO(Store store) {
        StoreCreationDTO dto = new StoreCreationDTO();
        dto.setId(store.getId());
        dto.setName(store.getName());
        dto.setAddress(store.getLocation().getAddress());
        dto.setLatitude(store.getLocation().getLatitude());
        dto.setLongitude(store.getLocation().getLongitude());
        dto.setInitialStock(store.getPhysicalStock());
        return dto;
    }
}