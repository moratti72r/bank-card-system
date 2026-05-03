package org.example.notification.service;

import com.example.common.dto.NotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @KafkaListener(topics = "${app.kafka.topic.admin-notification}",
            groupId = "bank-cards-group-debug",
            containerFactory = "notificationKafkaListenerContainerFactory")
    public void listenAdminNotification(NotificationDto message) {
        log.info("Получено ADMIN сообщение из Kafka: {}", message);

        UUID userId = message.getUserId();
        Map<String,Object> payload = message.getPayload();

        switch (message.getEventType()) {
            case USER_lOGOUT -> {
                System.out.printf("Пользователь c id - %s разлогинился", userId);
            }
            case USER_DELETED -> {
                System.out.printf("Пользователя c id - %s удален", payload.get("deleteUserId"));
            }
            case USER_TO_ADMIN -> {
                System.out.printf("Пользователь с email - %s переведен в администраторы", payload.get("userEmail"));
            }
            case CARD_BY_ID -> {
                System.out.printf("Получена информация о карте с номером - %s и владельцем с id - %s", payload.get("cardNumber"), payload.get("ownerCard"));
            }
            case CARD_DELETED -> {
                System.out.printf("Карта %s владельца с id - %s удаленна", payload.get("cardNumber"), payload.get("ownerCard"));
            }
            case CARDS_WAITING_BLOCK -> {
                System.out.printf("Получены %s карт ожидающие блокировку", payload.get("countCards"));
            }
            case CARDS_ALL -> {
                System.out.printf("Получены %s карт", payload.get("countCards"));
            }
            case CARDS_BY_PARAMETERS -> {
                System.out.printf("Получено %s карт по параметрам карты", payload.get("countCards"));
            }
            default -> throw new IllegalStateException("Unexpected value: " + message.getEventType());
        }
    }

    @KafkaListener(topics = "${app.kafka.topic.user-notification}",
            groupId = "bank-cards-group-debug",
            containerFactory = "notificationKafkaListenerContainerFactory")
    public void listenUserNotification(NotificationDto message) {
        log.info("Получено USER сообщение из Kafka: {}", message);

        Map<String,Object> payload = message.getPayload();


        switch(message.getEventType()) {
            case USER_REGISTERED -> {
                System.out.printf("Пользователь с email - %s успешно зарегистрирован", payload.get("userEmail"));
            }
            case USER_LOGGED_IN-> {
                System.out.printf("Выполнен вход пользователя %s ", payload.get("userEmail"));
            }
            case USER_UPDATED -> {
                System.out.printf("Пользователь обновил данные");
            }
            case CARD_CREATED -> {
                System.out.printf("Зарегистрирована новая карта %s", payload.get("cardNumber"));
            }
            case CARD_ACTIVATED -> {
                System.out.printf("Карта %s активирована", payload.get("cardNumber"));
            }
            case CARD_BALANCE -> {
                System.out.printf("Ваш баланс составляет %s рублей", payload.get("balance"));
            }
            case CARDS_ALL_BY_USER -> {
                System.out.printf("Получены все карты");
            }
            case CARD_BLOCKED -> {
                System.out.printf("Карта %s заблокиорована", payload.get("cardNumber"));
            }
            case TRANSFER -> {
                System.out.printf("Перевод в сумме %s выполнена", payload.get("amount"));
            }
        }
    }
}
