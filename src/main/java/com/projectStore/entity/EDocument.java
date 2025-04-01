package com.projectStore.entity;

import lombok.*;

@Getter
@RequiredArgsConstructor
public enum EDocument {
    CEDULA_CIUDADANIA("Cédula de Ciudadanía"),
    CEDULA_EXTRANJERIA("Cédula de Extranjería"),
    PASAPORTE("Pasaporte"),
    TARJETA_IDENTIDAD("Tarjeta de Identidad"),
    REGISTRO_CIVIL("Registro Civil"),
    PERMISO_ESPECIAL_PERMANENCIA("Permiso Especial de Permanencia");

    private final String description;
}