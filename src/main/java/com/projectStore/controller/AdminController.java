package com.projectStore.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projectStore.dto.StoreCreationDTO;
import com.projectStore.entity.Store;
import com.projectStore.entity.User;
import com.projectStore.service.StoreService;
import com.projectStore.service.UserService;
import com.projectStore.service.AuditService;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StoreService storeService;
    private final UserService userService;
    private final AuditService auditService;

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    // Dashboard principal para administradores
    @GetMapping
    public ModelAndView dashboard(Model model, Principal principal) {
            // Obtener el usuario actual por su nombre de usuario
            User currentUser = userService.findByEmail(principal.getName());
            
            // Obtener las tiendas administradas por el usuario actual
            List<Store> stores = storeService.getStoresByAdmin(currentUser);
            
            model.addAttribute("stores", stores);
            return new ModelAndView("dashboardAdmin");
    }

    // Página para ver todas las tiendas
    @GetMapping("/adminStores")
    public String getAllStores(Model model) {
        try {
            model.addAttribute("stores", storeService.getAllStores());
            return "adminStores";
        } catch (Exception e) {
            model.addAttribute("error", "Error cargando tiendas: " + e.getMessage());
            return "error";
        }
    }

    // Página para ver las tiendas del administrador actual
    @GetMapping("/my-stores")
    public String getMyStores(Model model, Principal principal) {
        try {
            User currentUser = userService.findByEmail(principal.getName());
            if (currentUser == null) {
                model.addAttribute("error", "Usuario no autenticado");
                return "error";
            }
            
            List<Store> stores = storeService.getStoresByAdmin(currentUser);
            model.addAttribute("stores", stores);
            return "my-stores";
        } catch (Exception e) {
            model.addAttribute("error", "Error cargando tiendas: " + e.getMessage());
            return "error";
        }
    }

    // Página para ver detalles de una tienda específica
    @GetMapping("/stores/{id}")
    public String getStoreById(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("store", storeService.getStoreById(id));
            return "store-details";
        } catch (Exception e) {
            model.addAttribute("error", "Error cargando tienda: " + e.getMessage());
            return "error";
        }
    }

    // Página para crear una nueva tienda
    @GetMapping("/stores/create")
    public String createStorePage(Model model) {
        try {
            model.addAttribute("googleMapsApiKey", googleMapsApiKey);
            model.addAttribute("storeDTO", new StoreCreationDTO());
            return "create-store";
        } catch (Exception e) {
            model.addAttribute("error", "Error cargando formulario: " + e.getMessage());
            return "create-store";
        }
    }

    // Crear una nueva tienda
    @PostMapping("/stores/create")
    public String createStore(
            @ModelAttribute StoreCreationDTO storeDTO,
            Principal principal,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            String email = principal.getName();
            User admin = userService.findByEmail(email);
            
            if (admin == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no autorizado");
                return "redirect:/admin/adminStores";
            }
            
            String ipAddress = request.getRemoteAddr();
            storeService.createStore(storeDTO, admin.getId());
            
            redirectAttributes.addFlashAttribute("success", "Tienda creada correctamente");
            return "redirect:/admin/my-stores";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creando tienda: " + e.getMessage());
            return "redirect:/admin/stores/create";
        }
    }

    // Página para editar una tienda existente
    @GetMapping("/stores/{id}/edit")
    public String editStorePage(@PathVariable Long id, Model model, Principal principal) {
        try {
            String email = principal.getName();
            User admin = userService.findByEmail(email);
            
            Store store = storeService.getStoreEntityById(id);
            
            // Verificar que el usuario sea el administrador de esta tienda
            if (!store.getAdmin().getId().equals(admin.getId())) {
                model.addAttribute("error", "No tienes permisos para editar esta tienda");
                return "error";
            }
            
            model.addAttribute("storeDTO", storeService.convertToCreationDTO(store));
            return "edit-store";
        } catch (Exception e) {
            model.addAttribute("error", "Error cargando tienda: " + e.getMessage());
            return "error";
        }
    }

    // Actualizar una tienda existente
    @PostMapping("/stores/{id}/update")
    public String updateStore(
            @PathVariable Long id, 
            @ModelAttribute StoreCreationDTO storeDTO,
            Principal principal,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            String email = principal.getName();
            User admin = userService.findByEmail(email);
            
            if (admin == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no autorizado");
                return "redirect:/admin/stores";
            }
            
            // Validar que el ID de la tienda en la ruta y el DTO coincidan
            if (!id.equals(storeDTO.getId())) {
                throw new RuntimeException("ID de tienda no coincide con el del cuerpo de la solicitud");
            }
            
            String ipAddress = request.getRemoteAddr();
            Store updatedStore = storeService.updateStore(storeDTO);
            
            redirectAttributes.addFlashAttribute("success", "Tienda actualizada correctamente");
            return "redirect:/admin/my-stores";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error actualizando tienda: " + e.getMessage());
            return "redirect:/admin/stores/" + id + "/edit";
        }
    }

    // Eliminar una tienda
    @PostMapping("/stores/{id}/delete")
    public String deleteStore(
            @PathVariable Long id,
            Principal principal,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        try {
            String email = principal.getName();
            User admin = userService.findByEmail(email);
            
            if (admin == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no autorizado");
                return "redirect:/admin/stores";
            }
            
            String ipAddress = request.getRemoteAddr();
            
            // Obtener nombre de la tienda antes de eliminarla para incluirlo en la auditoría
            String storeName = storeService.getStoreById(id).getName();
            
            // Eliminar la tienda
            storeService.deleteStore(id, admin.getId());
            
            redirectAttributes.addFlashAttribute("success", "Tienda eliminada correctamente");
            return "redirect:/admin/my-stores";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error eliminando tienda: " + e.getMessage());
            return "redirect:/admin/my-stores";
        }
    }
}