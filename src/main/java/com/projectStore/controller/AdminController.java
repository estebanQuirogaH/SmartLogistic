package com.projectStore.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.projectStore.dto.StoreCreationDTO;
import com.projectStore.dto.StoreDTO;
import com.projectStore.entity.User;
import com.projectStore.mapper.StoreMapper;
import com.projectStore.service.StoreService;
import com.projectStore.service.UserService;
import com.projectStore.entity.Store;

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
     * Obtiene las tiendas creadas por el administrador actual usando Firebase Authentication
     */
    @GetMapping("/my-stores")
    public ResponseEntity<List<StoreDTO>> getMyStores(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String firebaseToken = authorizationHeader.substring(7);
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
            String email = decodedToken.getEmail();

            User currentUser = userService.findByEmail(email);
            if (currentUser == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            // Obtener las tiendas del usuario
            List<Store> stores = storeService.getStoresByAdmin(currentUser);

            // Convertir a DTOs usando StoreMapper
            List<StoreDTO> storeDTOs = StoreMapper.INSTANCE.toDTOList(stores);

            return new ResponseEntity<>(storeDTOs, HttpStatus.OK);
        } catch (FirebaseAuthException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
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
    public ResponseEntity<StoreDTO> createStore(@RequestBody StoreCreationDTO storeDTO,
                                                @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Se espera que el header venga como "Bearer <token>"
            String firebaseToken = authorizationHeader.substring(7);
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
            String email = decodedToken.getEmail();

            // Buscar el usuario admin en la base de datos usando el email obtenido de Firebase
            User admin = userService.findByEmail(email);
            if (admin == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            // Crear la tienda utilizando el ID del admin
            StoreDTO createdStore = storeService.createStore(storeDTO, admin.getId());
            return new ResponseEntity<>(createdStore, HttpStatus.CREATED);
        } catch (FirebaseAuthException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Actualiza una tienda existente
     */
    @PutMapping("/stores/{id}")
    public ResponseEntity<Store> updateStore(@PathVariable Long id,
                                             @RequestBody StoreCreationDTO storeDTO,
                                             @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extraer el token de Firebase (se espera que venga como "Bearer <token>")
            String firebaseToken = authorizationHeader.substring(7);
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
            String email = decodedToken.getEmail();

            // Buscar el usuario en la base de datos usando el email obtenido de Firebase
            User currentUser = userService.findByEmail(email);
            if (currentUser == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            // Validar que el ID de la tienda en la ruta y el DTO coincidan
            if (!id.equals(storeDTO.getId())) {
                throw new RuntimeException("ID de tienda no coincide con el del cuerpo de la solicitud");
            }

            // Actualizar la tienda
            Store updatedStore = storeService.updateStore(storeDTO);
            return new ResponseEntity<>(updatedStore, HttpStatus.OK);
        } catch (FirebaseAuthException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Elimina una tienda existente
     */
    @DeleteMapping("/stores/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Se espera que el header venga como "Bearer <token>"
            String firebaseToken = authorizationHeader.substring(7);
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
            String email = decodedToken.getEmail();

            // Buscar el usuario admin en la base de datos usando el email obtenido de Firebase
            User admin = userService.findByEmail(email);
            if (admin == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            // Llamar al service para eliminar la tienda usando el ID del admin autenticado
            storeService.deleteStore(id, admin.getId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (FirebaseAuthException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}