package com.banking.system.service;

import com.banking.system.web.dto.AuditLogDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    Page<AuditLogDto> getAllAuditLogs(Pageable pageable);
    Page<AuditLogDto> getAuditLogsByEntity(String entityName, Pageable pageable);
}
