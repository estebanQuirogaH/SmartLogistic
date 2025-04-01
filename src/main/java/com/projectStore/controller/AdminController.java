package com.projectStore.controller;

import com.projectStore.dto.StoreCreationDTO;
import com.projectStore.dto.StoreDTO;
import com.projectStore.entity.User;
import com.projectStore.service.StoreService;
import com.projectStore.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private UserService userService;

    /**
     * Obtiene todas las tiendas disponibles
     */
    @GetMapping("/stores")
    public ResponseEntity<List<StoreDTO>> getAllStores() {
        List<StoreDTO> stores = storeService.getAllStores();
        return new ResponseEntity<>(stores, HttpStatus.OK);
    }

    /**
     * Obtiene las tiendas creadas por el administrador actual
     */
    @GetMapping("/my-stores")
    public ResponseEntity<List<StoreDTO>> getMyStores() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findById(auth.getId());
        List<StoreDTO> stores = storeService.getStoresByAdmin(currentUser);
        return new ResponseEntity<>(stores, HttpStatus.OK);
    }

    /**
     * Obtiene una tienda específica por ID
     */
    @GetMapping("/stores/{id}")
    public ResponseEntity<StoreDTO> getStoreById(@PathVariable Long id) {
        StoreDTO store = storeService.getStoreById(id);
        return new ResponseEntity<>(store, HttpStatus.OK);
    }

    /**
     * Crea una nueva tienda
     */
    @PostMapping("/stores")
    public ResponseEntity<StoreDTO> createStore(@RequestBody StoreCreationDTO storeDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findById(auth.getId());

        StoreDTO createdStore = storeService.createStore(storeDTO, currentUser.getId());
        return new ResponseEntity<>(createdStore, HttpStatus.CREATED);
    }

    /**
     * Actualiza una tienda existente
     */
    @PutMapping("/stores/{id}")
    public ResponseEntity<Store> updateStore(@PathVariable Long id, @RequestBody StoreCreationDTO storeDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findById(auth.getid());

        StoreCreationDTO updatedStore = storeService.updateStore(storeDTO);
        return new ResponseEntity<>(updatedStore, HttpStatus.OK);
    }

    /**
     * Elimina una tienda existente
     */
    @DeleteMapping("/stores/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findById(auth.getId());

        storeService.deleteStore(id, currentUser.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}