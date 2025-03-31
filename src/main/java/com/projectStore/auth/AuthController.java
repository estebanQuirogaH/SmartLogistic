package com.projectStore.auth;

import com.google.firebase.auth.FirebaseAuthException;
import com.projectStore.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String email) {
        try {
            return authService.authenticateUser(email);
        } catch (FirebaseAuthException e) {
            return "Error: " + e.getMessage();
        }
    }
}