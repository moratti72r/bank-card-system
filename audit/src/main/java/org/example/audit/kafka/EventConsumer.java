package org.example.audit.kafka;

import com.example.common.message.EventMessage;
import lombok.RequiredArgsConstructor;
import org.example.audit.auditor.service.AuditLogService;
import org.example.audit.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventConsumer {

    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    @KafkaListener(topics = "${app.kafka.topic.audit-event}", groupId = "bank-cards-group-debug")
    public void listenEvent(EventMessage message) {
        log.info("Получено сообщение из Kafka: {}", message);

        notificationService.sendMessage(message);
        auditLogService.saveEvent(message);
    }
}
