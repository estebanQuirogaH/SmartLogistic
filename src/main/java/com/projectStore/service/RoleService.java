package com.projectStore.service;

import com.projectStore.entity.RoleEntity;
import com.projectStore.repository.RoleRepository;

public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    // public User findByEmail(String email) {
    // return userRepository.findByEmail(email)
    // .orElseThrow(() -> new IllegalArgumentException("User not found with email: "
    // + email));
    // }

    public RoleEntity findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with name: " + name));
    }
}
