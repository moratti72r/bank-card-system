package com.example.bankcards.kafka;

import com.example.common.message.EventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventProducer {

    @Value("${app.kafka.topic.audit-event}")
    private String topic;

    private final KafkaTemplate<String, EventMessage> kafkaTemplate;

    @Async
    public void sendEvent(EventMessage event) {
        log.info("Отправляю описание события в Kafka: {}", event);

        kafkaTemplate.send(topic, event.getUserId(), event).whenComplete((result, e) -> {
            if (e != null) log.error("Не удалось отправить сообщение", e);
        });
    }
}

