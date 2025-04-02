package com.projectStore.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.projectStore.entity.Parameter;
import com.projectStore.entity.RoleEntity;
import com.projectStore.entity.User;
import com.projectStore.repository.ParameterRepository;
import com.projectStore.repository.RoleRepository;
import com.projectStore.repository.UserRepository;
import com.projectStore.service.RoleService;
import com.projectStore.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class InitialSetup implements ApplicationRunner {

    private final ParameterRepository parameterRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        try {
            log.info("Iniciando configuración inicial...");

            // Crear roles
            createRoleIfNotExists("ROLE_SUPERADMIN");
            createRoleIfNotExists("ROLE_ADMIN");
            createRoleIfNotExists("ROLE_USER");

            // Crear usuario superadmin
            createSuperAdminIfNotExists();

            // Crear parámetros
            createParameterIfNotExists("Distancia", "1.0",
                    "Distancia mínima en kilómetros entre tiendas físicas");
            createParameterIfNotExists("porcentaje stock virtual", "20",
                    "Porcentaje de stock para tienda virtual por defecto");

            log.info("Configuración inicial completada exitosamente");
        } catch (Exception e) {
            log.error("Error en la configuración inicial: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    private void createSuperAdminIfNotExists() {
        String email = "juan@example.com";
        String password = "password";

        try {
            // Primero verificamos si existe el rol
            RoleEntity superAdminRole = roleRepository.findByName("ROLE_SUPERADMIN")
                    .orElseThrow(() -> new RuntimeException("Rol ROLE_SUPERADMIN no encontrado"));

            // Verificamos si existe el usuario
            Optional<User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isEmpty()) {
                log.info("Creando superadmin inicial...");

                User superAdmin = new User();
                superAdmin.setEmail(email);
                superAdmin.setPassword(passwordEncoder.encode(password));

                // Inicializamos la lista de roles
                List<RoleEntity> roles = new ArrayList<>();
                roles.add(superAdminRole);
                superAdmin.setRoles(roles);

                userRepository.save(superAdmin);
                log.info("Superadmin creado exitosamente con email: {}", email);
            } else {
                log.info("El superadmin ya existe con email: {}", email);
            }
        } catch (Exception e) {
            log.error("Error creando superadmin: {}", e.getMessage(), e);
            throw new RuntimeException("Error creando superadmin: " + e.getMessage(), e);
        }
    }

    @Transactional
    private void createRoleIfNotExists(String roleName) {
        try {
            if (roleRepository.findByName(roleName).isEmpty()) {
                RoleEntity role = new RoleEntity();
                role.setName(roleName);
                roleRepository.save(role);
            }
        } catch (Exception e) {
            log.error("Error creando rol {}: {}", roleName, e.getMessage());
            throw new RuntimeException("Error creando rol: " + roleName, e);
        }
    }

    private void createParameterIfNotExists(String name, String value, String description) {
        if (!parameterRepository.findByName(name).isPresent()) {
            Parameter parameter = new Parameter();
            parameter.setName(name);
            parameter.setValue(value);
            parameter.setDescription(description);
            parameter.setLastModified(new Date());
            parameterRepository.save(parameter);
        }
    }
}