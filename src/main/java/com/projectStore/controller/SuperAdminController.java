package com.projectStore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.projectStore.dto.AdminCreationDTO;
import com.projectStore.entity.EDocument;
import com.projectStore.mapper.ParameterMapper;
import com.projectStore.service.AdminService;
import com.projectStore.service.AuditService;
import com.projectStore.service.ParameterService;
import com.projectStore.service.StoreService;
// import com.projectStore.entity.EDocument;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.Arrays;

@Controller
@RequestMapping("/superadmin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final ParameterService parameterService;
    private final AdminService adminService;
    private final AuditService auditService;
    private final StoreService storeService;
    // private final ParameterMapper parameterMapper;

    // Página principal
    @GetMapping
    public String superAdminDashboard(Model model) {
        model.addAttribute("parameters", parameterService.getAllParameters());
        model.addAttribute("admins", adminService.getAllAdmins());
        model.addAttribute("stores", storeService.getAllStores());
        return "dashboard";
    }

    // Página de configuración de parámetros
    @GetMapping("/parameters")
    public ModelAndView parametersPage(Model model) {
        model.addAttribute("parameters", parameterService.getAllParameters());
        return new ModelAndView("parameters");
    }

    // Actualizar parámetro
    @PostMapping("/parameters/update")
    public String updateParameter(
            @RequestParam String name,
            @RequestParam String value,
            HttpServletRequest request,
            Principal principal) {

        String ipAddress = request.getRemoteAddr();
        parameterService.updateParameter(name, value, ipAddress, principal.getName());
        return "redirect:/superadmin/parameters";
    }

    // Página de creación de administradores
    @GetMapping("/admins/create")
    public ModelAndView createAdminPage(Model model) {
        model.addAttribute("stores", storeService.getAllStores());
        model.addAttribute("documentTypes", Arrays.asList(EDocument.values()));
        return new ModelAndView("create-admin");
    }

    // Crear administrador
    @PostMapping("/admins/create")
    public String createAdmin(
            @ModelAttribute AdminCreationDTO dto,
            HttpServletRequest request,
            Principal principal) {

        String ipAddress = request.getRemoteAddr();
        adminService.createAdmin(dto, ipAddress, principal.getName());
        return "redirect:/admins";
    }

    // Página de auditorías
    @GetMapping("/audits")
    public String auditsPage(Model model) {
        model.addAttribute("audits", auditService.getAllAudits());
        return "audits";
    }

    @GetMapping("/logout")
    public String logoutPage(Model model) {
        // model.addAttribute("audits", auditService.getAllAudits());
        return "parameters";
    }
}
