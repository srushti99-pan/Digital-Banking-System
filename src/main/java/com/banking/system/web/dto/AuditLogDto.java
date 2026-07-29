package com.banking.system.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuditLogDto {
    private Long id;
    private String entityName;
    private Long entityId;
    private String action;
    private String changedBy;
    private LocalDateTime changedAt;
    private String details;
}
