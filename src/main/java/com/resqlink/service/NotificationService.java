package com.resqlink.service;

import com.resqlink.model.AuditLog;
import com.resqlink.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final AuditLogRepository auditLogRepository;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate, AuditLogRepository auditLogRepository) {
        this.messagingTemplate = messagingTemplate;
        this.auditLogRepository = auditLogRepository;
    }

    public void logAndBroadcast(String entityType, Long entityId, String action, String performedBy, String title, String message, String alertType) {
        // 1. Persist audit log
        try {
            AuditLog auditLog = new AuditLog(entityType, entityId, action, performedBy, message);
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            logger.error("Failed to save audit log: {}", e.getMessage());
        }

        // 2. Broadcast live WebSocket message
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("entityType", entityType);
            payload.put("entityId", entityId);
            payload.put("action", action);
            payload.put("title", title);
            payload.put("message", message);
            payload.put("alertType", alertType); // danger, warning, success, info
            payload.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend("/topic/alerts", payload);
            logger.info("Broadcasted live alert: [{}] {}", title, message);
        } catch (Exception e) {
            logger.warn("WebSocket push failed (possibly no active clients yet): {}", e.getMessage());
        }
    }
}
