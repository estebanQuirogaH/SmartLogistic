// package com.projectStore.service;

// import com.projectStore.dto.AdminCreationDTO;
// import com.projectStore.entity.*;
// import com.projectStore.repository.RoleRepository;
// import com.projectStore.repository.UserRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

// @Service
// @RequiredArgsConstructor
// public class AdminServiceImpl implements AdminService {

//     private final UserRepository userRepository;
//     private final RoleRepository roleRepository;
//     private final UserService userService;
//     private final RoleService roleService;
//     private final StoreService storeService;
//     private final AuditService auditService;
//     private final PasswordEncoder passwordEncoder;

//     @Override
//     @Transactional
//     public User createAdmin(AdminCreationDTO dto, String ipAddress, String creatorUsername) {
//         User superAdmin = userService.findByEmail(creatorUsername);

//         // Crear Persona y Documento
//         Person person = new Person();
//         person.setName(dto.getName());

//         Document document = new Document();
//         document.setDocumentType(EDocument.valueOf(dto.getDocumentType()));
//         document.setDocumentNumber(dto.getDocumentNumber());
//         person.setDocument(document);

//         // Crear usuario administrador con contraseña encriptada
//         User admin = new User();
//         admin.setEmail(dto.getEmail());
//         admin.setPassword(passwordEncoder.encode(dto.getPassword())); // Encriptar

//         // Asignar rol
//         List<RoleEntity> roles = new ArrayList<>();
//         roles.add(roleService.findByName("ROLE_ADMIN"));
//         admin.setRoles(roles);

//         // Guardar usuario
//         User savedAdmin = userService.saveUser(admin);

//         // Asignar tiendas
//         if (dto.getAssignedStoreIds() != null && !dto.getAssignedStoreIds().isEmpty()) {
//             for (Long storeId : dto.getAssignedStoreIds()) {
//                 Store store = storeService.getStoreById(storeId);
//                 store.setAdmin(savedAdmin);
//                 storeService.updateStore(store);

//                 auditService.registerAudit(
//                         ipAddress,
//                         "Tienda asignada a administrador: " + store.getName() + " -> " + admin.getEmail(),
//                         superAdmin,
//                         store
//                 );
//             }
//         }

//         // Registrar auditoría de creación
//         auditService.registerAudit(
//                 ipAddress,
//                 "Creación de administrador: " + admin.getEmail(),
//                 superAdmin,
//                 null
//         );

//         return savedAdmin;
//     }

//     @Override
//     public List<User> getAllAdmins() {
//         RoleEntity adminRole = roleService.findByName("ROLE_ADMIN");
//         return userService.findByRole(adminRole);
//     }

//     public User findAdminById(Long id) {
//         return userRepository.findById(id)
//                 .filter(user -> user.getRoles().stream().anyMatch(rol -> rol.getName().equals("ROLE_ADMIN")))
//                 .orElse(null);
//     }

//     public List<User> findAllAdmins() {
//         List<User> allUsers = userRepository.findAll();
//         List<User> admins = new ArrayList<>();

//         for (User user : allUsers) {
//             boolean isAdmin = user.getRoles().stream()
//                 .anyMatch(rol -> rol.getName().equals("ROLE_ADMIN"));
//             if (isAdmin) {
//                 admins.add(user);
//             }
//         }

//         return admins;
//     }

//     public boolean canAdminManageStore(Long adminId, Long storeId) {
//         User user = userRepository.findById(adminId).orElse(null);
//         if (user == null) {
//             return false;
//         }

//         boolean isSuperAdmin = user.getRoles().stream()
//             .anyMatch(rol -> rol.getName().equals("ROLE_SUPERADMIN"));

//         if (isSuperAdmin) {
//             return true;
//         }

//         // Aquí debería ir la validación real de la tienda
//         return true; 
//     }
// }
