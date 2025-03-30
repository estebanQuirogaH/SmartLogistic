package com.projectStore.mapper;

import org.mapstruct.Mapper;

import com.projectStore.dto.ParameterDTO;
import com.projectStore.entity.Parameter;

@Mapper(componentModel = "spring")
public interface ParameterMapper {

    // Convierte de Parameter a ParameterDTO
    ParameterDTO toDTO(Parameter parameter);

    // Convierte de ParameterDTO a Parameter
    Parameter toEntity(ParameterDTO dto);
}
