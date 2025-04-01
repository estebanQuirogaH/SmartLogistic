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
import com.projectStore.entity.Store;
import com.projectStore.entity.User;
import com.projectStore.mapper.StoreMapper;
import com.projectStore.repository.ParameterRepository;
import com.projectStore.repository.StoreRepository;
import com.projectStore.repository.UserRepository;

import jakarta.persistence.Parameter;

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

    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private ParameterRepository parameterRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StoreMapper storeMapper;
    
    @Autowired
    private LocationService locationService;
    
    @Autowired
    private AuditService auditService;

    @Autowired
    private ParameterService parameterService;

    //obtener todas las tiendas
    // public List<Store> getAllStores() {
    //     return storeRepository.findAll();
    // }
    public List<StoreDTO> getAllStores() {
        List<Store> stores = storeRepository.findAll();
        return storeMapper.toDTOList(stores);
    }

    // public Store getStoreById(Long id) {
    //     return storeRepository.findById(id)
    //             .orElseThrow(() -> new RuntimeException("Tienda no encontrada: " + id));
    // }

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
    //     // Validar distancia mínima con otras tiendas
    //     if (!isLocationValid(store.getLocation())) {
    //         throw new RuntimeException("La ubicación de la tienda está demasiado cerca de otra tienda existente.");
    //     }
    //     // Establecer el porcentaje de stock virtual
    //     store.setVirtualStockPercentage(parameterService.getVirtualStockPercentage());
    //     Store savedStore = storeRepository.save(store);
    //     // Registrar auditoría
    //     auditService.registerAudit(
    //             ipAddress,
    //             "Creación de tienda: " + store.getName(),
    //             creator,
    //             savedStore);
    //     return savedStore;
    // }

        /**
     * Crea una nueva tienda
     */

     public StoreDTO createStore(StoreCreationDTO storeDTO, Long creatorId) {
        // Verificar que el usuario existe
        User creator = userRepository.findById(creatorId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario creador no válido"));
    
        // Obtener coordenadas desde la dirección si no hay latitud/longitud
        Location location = (storeDTO.getLatitude() != null && storeDTO.getLongitude() != null) 
            ? new Location(storeDTO.getAddress(), storeDTO.getLatitude(), storeDTO.getLongitude()) 
            : locationService.getCoordinatesFromAddress(storeDTO.getAddress());
    
        if (location == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo obtener coordenadas para la dirección");
        }
    
        // Validar la ubicación
        if (!locationService.isValidStoreLocation(location)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "La ubicación no cumple con la distancia mínima requerida entre tiendas");
        }
    
        // Crear la tienda
        Store store = storeMapper.toEntity(storeDTO);
        store.setLocation(location); // Asignar ubicación
    
        // Calcular stock virtual
        calculateVirtualStock(store);
    
        // Guardar la tienda
        store = storeRepository.save(store);
    
        // // Registrar auditoría
        // auditService.logEvent("STORE_CREATED", "Admin ID=" + creatorId + " creó tienda: " + store.getName());
    
        return storeMapper.toDTO(store);
    }

    // @Transactional
    // public Store updateStore(Store store) {
    //     return storeRepository.save(store);
    // }

    /**
     * Actualiza una tienda existente
     */
    public StoreDTO updateStore(Long id, StoreCreationDTO storeDTO, Long adminId) {
        Store existingStore = storeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tienda no encontrada"));
    
        // if (!existingStore.getCreatorId().equals(adminId)) {
        //     throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
        //         "No tienes permiso para actualizar esta tienda");
        // }
    
        // Si la ubicación cambió, obtener nuevas coordenadas y validar
        if (!existingStore.getLocation().getAddress().equals(storeDTO.getAddress())) {
            Location newLocation = locationService.getCoordinatesFromAddress(storeDTO.getAddress());
    
            if (newLocation == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo obtener coordenadas para la dirección");
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
    
        // auditService.logEvent("STORE_UPDATED", "Admin ID=" + adminId + " actualizó tienda ID=" + id);
    
        return storeMapper.toDTO(existingStore);
    }


    // @Transactional
    // public void deleteStore(Long id, String ipAddress, User user) {
    //     Store store = getStoreById(id);
    //     storeRepository.delete(store);
    //     // Registrar auditoría
    //     auditService.registerAudit(
    //             ipAddress,
    //             "Eliminación de tienda: " + store.getName(),
    //             user,
    //             null);
    // }

     /**
     * Elimina una tienda existente
     */
    public void deleteStore(Long id, Long adminId) {
        // Verificar que la tienda existe
        Store existingStore = storeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tienda no encontrada"));
        
        // // Verificar que el admin es el creador de la tienda
        // if (!existingStore.getAdmin().equals(adminId)) {
        //     throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
        //         "No tienes permiso para eliminar esta tienda");
        // }
        
        // Eliminar la tienda
        storeRepository.delete(existingStore);
        
        // // Registrar la acción en la auditoría
        // auditService.logEvent(
        //     "STORE_DELETED", 
        //     "Admin ID=" + adminId + " eliminó tienda ID=" + id + " (" + existingStore.getName() + ")"
        // );
    }

    /**
     * Calcula el stock virtual basado en el stock físico y el porcentaje configurado
     */
    private void calculateVirtualStock(Store store) {
        // Obtener el parámetro de porcentaje para stock virtual
        Parameter virtualStockParam = parameterRepository.findByName("VIRTUAL_STOCK_PERCENTAGE");
        
        if (virtualStockParam != null) {
            try {
                virtualStockParam = Double.parseDouble(virtualStockParam.getValue());
            } catch (NumberFormatException e) {
                // Usar el valor predeterminado si hay un error al parsear
            }
        }
        
        // Calcular el stock virtual para cada producto
        Map<String, Integer> virtualStock = new HashMap<>();
        for (Map.Entry<String, Integer> entry : store.getPhysicalStock().entrySet()) {
            int physicalAmount = entry.getValue();
            int virtualAmount = (int) Math.floor(physicalAmount * (parameterRepository.findByName("VIRTUAL_STOCK_PERCENTAGE") / 100.0));
            virtualStock.put(entry.getKey(), virtualAmount);
        }
        
        store.setVirtualStock(virtualStock);
    }
}
