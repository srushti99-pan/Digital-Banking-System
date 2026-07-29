package com.banking.system.service;

import com.banking.system.domain.AuditLog;
import com.banking.system.repository.AuditLogRepository;
import com.banking.system.web.dto.AuditLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public Page<AuditLogDto> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    public Page<AuditLogDto> getAuditLogsByEntity(String entityName, Pageable pageable) {
        return auditLogRepository.findByEntityName(entityName, pageable).map(this::mapToDto);
    }

    private AuditLogDto mapToDto(AuditLog log) {
        AuditLogDto dto = new AuditLogDto();
        dto.setId(log.getId());
        dto.setEntityName(log.getEntityName());
        dto.setEntityId(log.getEntityId());
        dto.setAction(log.getAction());
        dto.setChangedBy(log.getChangedBy());
        dto.setChangedAt(log.getChangedAt());
        dto.setDetails(log.getDetails());
        return dto;
    }
}
