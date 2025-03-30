package com.projectStore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectStore.dto.AuditDTO;
import com.projectStore.entity.Audit;
import com.projectStore.entity.Store;
import com.projectStore.entity.User;
import com.projectStore.mapper.AuditMapper;
import com.projectStore.repository.AuditRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;
    private final AuditMapper auditMapper;

    @Transactional
    public Audit registerAudit(String ipAddress, String description, User user, Store store) {
        Audit audit = new Audit();
        audit.setTimestamp(new Date());
        audit.setIpAddress(ipAddress);
        audit.setDescription(description);
        audit.setUser(user);
        audit.setStore(store);

        return auditRepository.save(audit);
    }

    public List<AuditDTO> getAllAudits() {
        return auditRepository.findAllByOrderByTimestampDesc()
                .stream()
                .map(auditMapper::toDTO)
                .collect(Collectors.toList());
    }
}
