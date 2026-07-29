package com.banking.system.service;

import com.banking.system.domain.User;
import com.banking.system.domain.Notification;

import java.util.List;

public interface NotificationService {
    void sendNotification(User user, String subject, String message);
    List<Notification> getNotificationsForUser(Long userId);
}
