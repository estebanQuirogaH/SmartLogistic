package com.projectStore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projectStore.entity.RoleEntity;
import com.projectStore.entity.User;
import com.projectStore.repository.UserRepository; 

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // public User findByEmail(String email) {
    //     return userRepository.findByEmail(email)
    //             .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    // }

    // Método nuevo necesario para AdminController
    // public User findByUsername(String username) {
    //     return userRepository.findByUsername(username)
    //             .orElseThrow(() -> new RuntimeException("Usuario no encontrado con username: " + username));
    // }
    public User findbyId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado "));
    }
    
    // Método nuevo necesario para StoreMapper
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    public List<User> findByRole(RoleEntity adminRole) {
        return userRepository.findByRoles(adminRole);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

}
