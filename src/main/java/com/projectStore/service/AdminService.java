package com.projectStore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectStore.dto.AdminCreationDTO;
import com.projectStore.entity.Document;
import com.projectStore.entity.Person;
import com.projectStore.entity.Role;
import com.projectStore.entity.RoleEntity;
import com.projectStore.entity.Store;
import com.projectStore.entity.User;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;
    private final RoleService roleService;
    private final StoreService storeService;
    private final AuditService auditService;

    @Transactional
    public User createAdmin(AdminCreationDTO dto, String ipAddress, String creatorUsername) {
        // Buscamos el super administrador (quien crea el nuevo admin)
        User superAdmin = userService.findByEmail(creatorUsername);

        // Crear la persona y su documento
        // Person person = new Person();
        // person.setName(dto.getName());

        // Document document = new Document();
        // document.setType(EDocument.valueOf(dto.getDocumentType()));
        // document.setNumber(dto.getDocumentNumber());
        // person.setDocument(document);

        // Crear el usuario administrador
        User admin = new User();
        admin.setEmail(dto.getEmail());
        admin.setPassword(dto.getPassword()); // Nota: se debería encriptar la contraseña

        // Asignar el rol de administrador
        List<RoleEntity> roles = new ArrayList<>();
        roles.add(roleService.findByName("ROLE_ADMIN"));
        admin.setRoles(roles);

        // Guardar el usuario administrador
        User savedAdmin = userService.saveUser(admin);

        // Asignar tiendas al administrador y registrar auditoría para cada una
        if (dto.getAssignedStoreIds() != null && !dto.getAssignedStoreIds().isEmpty()) {
            for (Long storeId : dto.getAssignedStoreIds()) {
                Store store = storeService.getStoreById(storeId);
                store.setAdmin(savedAdmin);
                storeService.updateStore(store);

                auditService.registerAudit(
                        ipAddress,
                        "Tienda asignada a administrador: " + store.getName() + " -> " + admin.getEmail(),
                        superAdmin,
                        store);
            }
        }

        // Registrar auditoría de creación de administrador
        auditService.registerAudit(
                ipAddress,
                "Creación de administrador: " + admin.getEmail(),
                superAdmin,
                null);

        return savedAdmin;
    }

    public List<User> getAllAdmins() {
        Role adminRole = roleService.findByName("ROLE_ADMIN");
        return userService.findByRole(adminRole);
    }
}
