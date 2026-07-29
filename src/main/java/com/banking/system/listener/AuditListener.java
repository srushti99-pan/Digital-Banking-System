package com.banking.system.listener;

import com.banking.system.config.SpringContext;
import com.banking.system.domain.Account;
import com.banking.system.domain.AuditLog;
import com.banking.system.domain.Transaction;
import com.banking.system.domain.User;
import com.banking.system.repository.AuditLogRepository;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

public class AuditListener {

    @PostPersist
    public void onPostPersist(Object entity) {
        logAction(entity, "CREATE");
    }

    @PostUpdate
    public void onPostUpdate(Object entity) {
        logAction(entity, "UPDATE");
    }

    @PostRemove
    public void onPostRemove(Object entity) {
        logAction(entity, "DELETE");
    }

    private void logAction(Object entity, String action) {
        if (entity instanceof AuditLog) {
            return;
        }

        try {
            AuditLogRepository repository = SpringContext.getBean(AuditLogRepository.class);
            AuditLog log = new AuditLog();
            log.setChangedAt(LocalDateTime.now());
            
            String currentPrincipalName = "SYSTEM";
            if (SecurityContextHolder.getContext().getAuthentication() != null 
                    && SecurityContextHolder.getContext().getAuthentication().isAuthenticated() 
                    && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
                currentPrincipalName = SecurityContextHolder.getContext().getAuthentication().getName();
            }
            log.setChangedBy(currentPrincipalName);

            if (entity instanceof User) {
                User user = (User) entity;
                log.setEntityName("User");
                log.setEntityId(user.getId());
                log.setAction(action);
                log.setDetails("Username: " + user.getUsername() + ", Email: " + user.getEmail() + ", Enabled: " + user.isEnabled());
            } else if (entity instanceof Account) {
                Account account = (Account) entity;
                log.setEntityName("Account");
                log.setEntityId(account.getId());
                log.setAction(action);
                log.setDetails("AccountNumber: " + account.getAccountNumber() + ", Type: " + account.getAccountType() + ", Balance: " + account.getBalance() + ", Status: " + account.getStatus());
            } else if (entity instanceof Transaction) {
                Transaction tx = (Transaction) entity;
                log.setEntityName("Transaction");
                log.setEntityId(tx.getId());
                log.setAction(action);
                log.setDetails("Type: " + tx.getTransactionType() + ", Amount: " + tx.getAmount() + ", Status: " + tx.getStatus());
            } else {
                return;
            }

            repository.save(log);
        } catch (Exception e) {
            // Log to console but do not crash the transaction
            System.err.println("JPA Audit Log failed: " + e.getMessage());
        }
    }
}
