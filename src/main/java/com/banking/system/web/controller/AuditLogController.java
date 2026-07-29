package com.banking.system.web.controller;

import com.banking.system.service.AuditLogService;
import com.banking.system.web.dto.AuditLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<Page<AuditLogDto>> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("changedAt").descending());
        Page<AuditLogDto> result = auditLogService.getAllAuditLogs(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/entity/{entityName}")
    public ResponseEntity<Page<AuditLogDto>> getAuditLogsByEntity(
            @PathVariable String entityName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("changedAt").descending());
        Page<AuditLogDto> result = auditLogService.getAuditLogsByEntity(entityName, pageable);
        return ResponseEntity.ok(result);
    }
}
