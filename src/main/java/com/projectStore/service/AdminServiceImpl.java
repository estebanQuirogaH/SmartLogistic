package com.ProjectStore.services;

import com.ProjectStore.dto.AuditDTO;
import com.ProjectStore.entity.User;
import com.ProjectStore.entity.RolEntity;
import com.ProjectStore.repository.RolRepository;
import com.ProjectStore.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findAdminById(Long id) {
        Optional<User> admin = userRepository.findById(id);
        if (admin.isPresent()) {
            User user = admin.get();
            boolean isAdmin = user.getRoles().stream()
                .anyMatch(rol -> rol.getName().equals("ROLE_ADMIN"));
            
            if (isAdmin) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> findAllAdmins() {
        List<User> allUsers = userRepository.findAll();
        List<User> admins = new ArrayList<>();
        
        for (User user : allUsers) {
            boolean isAdmin = user.getRoles().stream()
                .anyMatch(rol -> rol.getName().equals("ROLE_ADMIN"));
            
            if (isAdmin) {
                admins.add(user);
            }
        }
        
        return admins;
    }

    @Override
    public boolean canAdminManageStore(Long adminId, Long storeId) {
        // Implementar lógica para verificar si el admin puede gestionar la tienda
        // (si es el creador o tiene permisos especiales)
        User user = userRepository.findById(adminId).orElse(null);
        if (user == null) {
            return false;
        }
        
        // Verificar si el usuario es superadmin (puede gestionar todas las tiendas)
        boolean isSuperAdmin = user.getRoles().stream()
            .anyMatch(rol -> rol.getName().equals("ROLE_SUPERADMIN"));
        
        if (isSuperAdmin) {
            return true;
        }
        
        // Verificar si el usuario es el creador de la tienda
        // Esta lógica asume que hay un método en StoreService que verifica esto
        return true; // Esto debe ser reemplazado con la lógica real
    }

    @Override
    public void logAdminAction(Long adminId, String action, String details) {
        User admin = userRepository.findById(adminId).orElse(null);
        String username = admin != null ? admin.getUsername() : "Unknown";
        
        AuditDTO auditDTO = new AuditDTO();
        auditDTO.setAction(action);
        auditDTO.setDetails("Admin: " + username + " - " + details);
        
        auditService.createAudit(auditDTO);
    }
}