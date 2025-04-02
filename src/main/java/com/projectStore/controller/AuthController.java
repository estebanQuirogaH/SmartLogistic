package com.projectStore.controller;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login")
    public ModelAndView showLoginPage() {
        return new ModelAndView("login");
    }

    @GetMapping("/success")
    public String loginSuccess(Principal principal, Authentication authentication) {
        // Verificar si el usuario tiene rol de SUPERADMIN
        boolean isSuperAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));
        
        // Redirigir según el rol
        if (isSuperAdmin) {
            return "redirect:/superadmin";
        } else {
            return "redirect:/admin";
        }
    }

  
}