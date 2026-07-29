package com.banking.system.service;

import com.banking.system.domain.Notification;
import com.banking.system.domain.User;
import com.banking.system.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void sendNotification(User user, String subject, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setSubject(subject);
        notification.setMessage(message);
        
        // Simulate email dispatch
        log.info("DISPATCHING EMAIL to {}: [{}] {}", user.getEmail(), subject, message);
        notification.setSent(true);
        notification.setSentAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId);
    }
}
