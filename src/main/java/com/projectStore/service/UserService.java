package com.projectStore.service;

import java.util.List;

import com.projectStore.entity.Role;
import com.projectStore.entity.User;
import com.projectStore.repository.UserRepository; // Asegúrate de tener este repositorio

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    public List<User> findByRole(Role adminRole) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByRole'");
    }

    public User saveUser(User admin) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveUser'");
    }

}
