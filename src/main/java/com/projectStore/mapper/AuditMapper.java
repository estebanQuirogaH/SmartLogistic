package com.projectStore.mapper;

import org.mapstruct.Mapper;

import com.projectStore.dto.AuditDTO;
import com.projectStore.entity.Audit;

@Mapper(componentModel = "spring")
public interface AuditMapper {

    AuditDTO toDTO(Audit audit);

    Audit toEntity(AuditDTO dto);
}
