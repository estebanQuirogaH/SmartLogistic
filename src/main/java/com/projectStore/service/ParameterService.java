package com.projectStore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import com.projectStore.entity.Parameter;
import com.projectStore.entity.User;
import com.projectStore.repository.ParameterRepository;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor // Se utiliza Lombok para inyección de dependencias sin tener que escribir el
                         // constructor con el @Autowired
// en cada campo
public class ParameterService {
    private final ParameterRepository parameterRepository;

    private final AuditService auditService;
    private final UserService userService;

    public Parameter getParameterByName(String name) {
        return parameterRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Parámetro no encontrado: " + name));
    }

    public List<Parameter> getAllParameters() {
        return parameterRepository.findAll();
    }

    @Transactional
    public Parameter updateParameter(String name, String value, String ipAddress,
            String username) {
        User currentUser = userService.findByEmail(username);
        Parameter parameter = getParameterByName(name);
        String oldValue = parameter.getValue();

        parameter.setValue(value);
        parameter.setLastModified(new Date());
        parameter.setModifiedBy(currentUser);

        Parameter saved = parameterRepository.save(parameter);

        // Registrar auditoría
        auditService.registerAudit(
                ipAddress,
                "Cambio en parámetro: " + name + " de '" + oldValue + "' a '" + value + "'",
                currentUser,
                null);

        return saved;
    }

    public double getMinimumDistanceBetweenStores() {
        return Double.parseDouble(getParameterByName("MIN_DISTANCE_BETWEEN_STORES").getValue());
    }

    public int getVirtualStockPercentage() {
        return Integer.parseInt(getParameterByName("DEFAULT_VIRTUAL_STOCK_PERCENTAGE").getValue());
    }
}
// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class ParameterService {

// private final ParameterRepository parameterRepository;
// private final UserService userService;
// private final AuditService auditService;

// public Parameter getParameterByName(String name) {

// return parameterRepository.findByName(name)
// .orElseThrow(() -> new RuntimeException("Parámetro no encontrado: " + name));
// }

// @Transactional
// public void updateParameter(String name, String value, String ipAddress,
// String username) {
// log.debug("Actualizando parámetro: {} con valor: {}", name, value);

// Parameter parameter = parameterRepository.findByName(name)
// .orElseThrow(() -> new RuntimeException("Parámetro no encontrado: " + name));

// User currentUser = userService.findByEmail(username);
// String oldValue = parameter.getValue();

// parameter.setValue(value);
// parameter.setLastModified(new Date());
// parameter.setModifiedBy(currentUser);

// parameterRepository.save(parameter);

// // Registrar la auditoría
// auditService.registerAudit(
// ipAddress,
// String.format("Parámetro %s actualizado de %s a %s", name, oldValue, value),
// currentUser,
// null);

// log.info("Parámetro {} actualizado exitosamente", name);
// }

// public List<Parameter> getAllParameters() {
// return parameterRepository.findAll();
// }

// public double getMinimumDistanceBetweenStores() {
// return
// Double.parseDouble(getParameterByName("MIN_DISTANCE_BETWEEN_STORES").getValue());
// }

// public int getVirtualStockPercentage() {
// return
// Integer.parseInt(getParameterByName("DEFAULT_VIRTUAL_STOCK_PERCENTAGE").getValue());
// }
