package com.projectStore.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projectStore.dto.AdminCreationDTO;
import com.projectStore.dto.AuditDTO;
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
import java.util.List;

@Controller
@RequestMapping("/superadmin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final ParameterService parameterService;
    private final AdminService adminService;
    private final AuditService auditService;
    private final StoreService storeService;

    // @GetMapping("/")
    // public String home() {
    // return "dashboard";
    // }

    // private final ParameterMapper parameterMapper;
    @GetMapping("/stores")
    public String storesPage(Model model) {
        model.addAttribute("stores", storeService.getAllStores());
        return "stores"; // Debe existir un stores.html en /resources/templates/
    }

    // Página principal
    @GetMapping
    public ModelAndView dashboard(Model model) {
        // try {
        model.addAttribute("parameters", parameterService.getAllParameters());
        model.addAttribute("admins", adminService.getAllAdmins());
        model.addAttribute("stores", storeService.getAllStores());
        return new ModelAndView("dashboard"); // dashboard.html
        // } catch (Exception e) {
        // model.addAttribute("error", "Error cargando el dashboard: " +
        // e.getMessage());
        // return "error"; // error.html
        // }
    }

    // Página de configuración de parámetros
    // @GetMapping("/parameters")
    // public ModelAndView parametersPage(Model model) {
    // model.addAttribute("parameters", parameterService.getAllParameters());
    // return new ModelAndView("parameters");
    // }
    @GetMapping("/parameters")
    public String parametersPage(Model model) {
        try {
            model.addAttribute("parameters", parameterService.getAllParameters());
            return "parameters"; // parameters.html
        } catch (Exception e) {
            model.addAttribute("error", "Error cargando parámetros: " + e.getMessage());
            return "error";
        }
    }
    // Actualizar parámetro

    @PostMapping("/parameters/update")
    public String updateParameter(
            @RequestParam String name,
            @RequestParam String value,
            HttpServletRequest request,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            String ipAddress = request.getRemoteAddr();
            String username = principal.getName();
            parameterService.updateParameter(name, value, ipAddress, username);
            redirectAttributes.addFlashAttribute("success", "Parámetro actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error actualizando parámetro: " + e.getMessage());
        }
        return "redirect:/superadmin/parameters";
    }

    // Página de creación de administradores
    @GetMapping("/admins/create")
    public String createAdminPage(Model model) {
        try {
            model.addAttribute("stores", storeService.getAllStores());
            model.addAttribute("documentTypes", EDocument.values());
            return "create-admin"; // create-admin.html
        } catch (Exception e) {
            model.addAttribute("error", "Error cargando formulario: " + e.getMessage());
            return "error";
        }
    }

    // Crear administrador
    @PostMapping("/admins/create")
    public String createAdmin(
            @ModelAttribute AdminCreationDTO dto,
            HttpServletRequest request,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            String ipAddress = request.getRemoteAddr();
            adminService.createAdmin(dto, ipAddress, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Administrador creado correctamente");
            return "redirect:/superadmin/admins/create";
        } catch (DataIntegrityViolationException e) {
            // Manejo específico para email duplicado
            if (e.getMessage().contains("UK6dotkott2kjsp8vw4d0m25fb7")) {
                redirectAttributes.addFlashAttribute("error",
                        "Ya existe un usuario con el email: " + dto.getEmail());
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "Error de integridad de datos al crear el administrador");
            }
            return "redirect:/superadmin/admins/create";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error inesperado al crear el administrador: " + e.getMessage());
            return "redirect:/superadmin/admins/create";
        }
    }

    @GetMapping("/admins")
    public String adminsPage(Model model) {
        try {
            model.addAttribute("admins", adminService.getAllAdmins());
            return "admins"; // admins.html
        } catch (Exception e) {
            model.addAttribute("error", "Error cargando administradores: " + e.getMessage());
            return "error";
        }
    }

    // Página de auditorías
    @GetMapping("/audits")
    public String auditsPage(Model model) {
        try {
            List<AuditDTO> audits = auditService.getAllAudits();
            model.addAttribute("audits", audits);
            return "audits";
        } catch (Exception e) {
            model.addAttribute("error", "Error cargando auditorías: " + e.getMessage());
            return "error";
        }
    }
}