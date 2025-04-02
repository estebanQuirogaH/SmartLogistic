package com.projectStore.service;

import java.io.FileInputStream;

import com.google.api.client.util.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.projectStore.entity.RoleEntity;
import com.projectStore.entity.User;
import com.projectStore.repository.RoleRepository;
import com.projectStore.repository.UserRepository;

import jakarta.annotation.PostConstruct;

import java.util.*;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FirebaseUserService {

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    @Value("${firebase.credentials.path}")
    private String firebaseCredentialsPath;

    private FirebaseAuth firebaseAuth;

    @PostConstruct
    public void init() {
        try {
            FileInputStream serviceAccount = new FileInputStream(firebaseCredentialsPath);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            firebaseAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando Firebase", e);
        }
    }

    public void syncUserFromFirebase(String firebaseUid) throws FirebaseAuthException {
        UserRecord userRecord = firebaseAuth.getUser(firebaseUid);

        // Verificar si el usuario ya existe en nuestra base de datos
        Optional<User> existingUser = userRepository.findByEmail(userRecord.getEmail());

        if (!existingUser.isPresent()) {
            // Crear nuevo usuario en nuestra base de datos
            User user = new User();
            user.setEmail(userRecord.getEmail());
            user.setPassword("FIREBASE_AUTH"); // No se almacena la contraseña real

            // Por defecto, asignar rol de usuario normal
            List<RoleEntity> roles = new ArrayList<>();
            Optional<RoleEntity> roleOptional = roleRepository.findByName("ROLE_USER");

            if (roleOptional.isPresent()) { // Verificamos si el rol existe
                roles.add(roleOptional.get()); // Extraemos el objeto RoleEntity
            } else {
                throw new RuntimeException("El rol ROLE_USER no existe en la base de datos");
            }

            user.setRoles(roles);

            userRepository.save(user);
        }
    }

    // Método para verificar token de Firebase
    public String verifyFirebaseToken(String idToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
        return decodedToken.getUid();
    }
}