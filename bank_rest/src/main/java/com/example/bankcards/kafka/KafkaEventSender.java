package com.example.bankcards.kafka;

import com.example.common.message.EventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KafkaEventSender {

    EventType type();

    boolean hasResult() default true;
    boolean hasArgs() default true;

}
