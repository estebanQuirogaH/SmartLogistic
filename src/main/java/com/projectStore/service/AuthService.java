package com.projectStore.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.projectStore.entity.RoleEntity;
import com.projectStore.entity.User;
import com.projectStore.repository.UserRepository;
import com.projectStore.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public String authenticateUser(String email) throws FirebaseAuthException {
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "User not found in the database.";
        }
        User user = userOpt.get();

        String roles = user.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.joining(";"));

        return jwtUtil.generateToken(email, roles);
    }
}
