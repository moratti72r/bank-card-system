package org.example.audit.notification;

import com.example.common.message.EventMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Async
    public void sendMessage(EventMessage message) {

        switch (message.getEventType()) {
            case USER_REGISTERED -> {
                System.out.println("Пользователь успешно зарегистрирован");
            }
            case USER_LOGGED_IN -> {
                System.out.println("Выполнен вход пользователя");
            }
            case USER_UPDATED -> {
                System.out.println("Пользователь обновил данные");
            }
            case CARD_CREATED -> {
                System.out.println("Зарегистрирована новая карта");
            }
            case CARD_ACTIVATED -> {
                System.out.println("Карта активирована");
            }
            case CARD_BALANCE -> {
                System.out.println("Получен баланс карты");
            }
            case CARD_BLOCKED -> {
                System.out.println("Карта заблокирована");
            }
            case TRANSFER -> {
                System.out.println("Перевод выполнен");
            }
        }
    }
}
