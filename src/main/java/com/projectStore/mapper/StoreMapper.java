package com.projectStore.mapper;

import com.projectStore.dto.StoreCreationDTO;
import com.projectStore.dto.StoreDTO;
import com.projectStore.entity.Location;
import com.projectStore.entity.Store;
import com.projectStore.entity.User;
import com.projectStore.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StoreMapper {

    @Autowired
    private UserService userService;

    /**
     * Convierte una entidad Store a un DTO
     */
    public StoreDTO toDTO(Store store) {
        if (store == null) {
            return null;
        }

        User creator = userService.findById(store.getCreatorId());
        String creatorName = creator != null ? creator.getUsername() : "Desconocido";

        return new StoreDTO(
            store.getId(),
            store.getName(),
            store.getLocation().getAddress(),
            store.getLocation().getLatitude(),
            store.getLocation().getLongitude(),
            store.getCreatorId(),
            creatorName,
            store.getCreatedAt(),
            store.getUpdatedAt(),
            store.getPhysicalStock(),
            store.getVirtualStock()
        );
    }

    /**
     * Convierte una lista de entidades Store a una lista de DTOs
     */
    public List<StoreDTO> toDTOList(List<Store> stores) {
        if (stores == null) {
            return null;
        }

        List<StoreDTO> storeDTOs = new ArrayList<>();
        for (Store store : stores) {
            storeDTOs.add(toDTO(store));
        }
        return storeDTOs;
    }

    /**
     * Crea una entidad Store a partir de un DTO de creación
     */
    public Store toEntity(StoreCreationDTO storeDTO, Long creatorId) {
        if (storeDTO == null) {
            return null;
        }

        Location location = new Location();
        location.setAddress(storeDTO.getAddress());
        location.setLatitude(storeDTO.getLatitude());
        location.setLongitude(storeDTO.getLongitude());

        Store store = new Store();
        store.setName(storeDTO.getName());
        store.setLocation(location);
        store.setCreatorId(creatorId);
        store.setCreatedAt(LocalDateTime.now());
        store.setUpdatedAt(LocalDateTime.now());
        
        // Inicializar el stock físico con los valores proporcionados
        Map<String, Integer> physicalStock = new HashMap<>();
        if (storeDTO.getInitialStock() != null) {
            physicalStock.putAll(storeDTO.getInitialStock());
        }
        store.setPhysicalStock(physicalStock);
        
        // El stock virtual se calculará basado en parámetros del sistema
        store.setVirtualStock(new HashMap<>());

        return store;
    }

    /**
     * Actualiza una entidad Store existente con los datos de un DTO
     */
    public void updateEntityFromDTO(Store store, StoreCreationDTO storeDTO) {
        if (store != null && storeDTO != null) {
            store.setName(storeDTO.getName());
            
            Location location = store.getLocation();
            if (location == null) {
                location = new Location();
                store.setLocation(location);
            }
            
            location.setAddress(storeDTO.getAddress());
            location.setLatitude(storeDTO.getLatitude());
            location.setLongitude(storeDTO.getLongitude());
            
            store.setUpdatedAt(LocalDateTime.now());
            
            // Actualizar el stock físico si se proporciona
            if (storeDTO.getInitialStock() != null) {
                store.setPhysicalStock(storeDTO.getInitialStock());
                // El stock virtual se recalculará en el servicio
            }
        }
    }
}