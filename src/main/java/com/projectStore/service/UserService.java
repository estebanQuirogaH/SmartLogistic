package com.projectStore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projectStore.entity.RoleEntity;
import com.projectStore.entity.User;
import com.projectStore.repository.UserRepository; // Asegúrate de tener este repositorio

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    public List<User> findByRole(RoleEntity adminRole) {
        return userRepository.findByRoles(adminRole);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

}
