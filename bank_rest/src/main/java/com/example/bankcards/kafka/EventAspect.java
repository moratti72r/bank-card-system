package com.example.bankcards.kafka;

import com.example.common.message.EventType;
import com.example.common.message.EventMessage;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

@Aspect
@Component
@RequiredArgsConstructor
public class EventAspect {

    private final EventProducer eventProducer;

    @Around("@annotation(event)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, KafkaEventSender event) throws Throwable {
        EventMessage.EventMessageBuilder eventBuilder = EventMessage.builder().eventType(event.type()).createdAt(LocalDateTime.now()).userId(getCurrentUserId()).role(getCurrentUserRoles());

        if (event.hasArgs() && !event.type().equals(EventType.USER_REGISTERED) && !event.type().equals(EventType.USER_LOGGED_IN)) {
            eventBuilder.methodArgs(extractMethodArgs(joinPoint));
        }

        try {
            Object result = joinPoint.proceed();

            if (event.hasResult()) {
                Map<String, Object> methodResult = result instanceof List ? Map.of("resultSize", ((List<?>) result).size()) : objectToMap(result);
                eventBuilder.methodResult(methodResult);
            }
            eventProducer.sendEvent(eventBuilder.build());

            return result;

        } catch (Exception e) {
            eventBuilder.isSuccess(false).methodResult(Map.of("exceptionMessage", e.getMessage()));

            eventProducer.sendEvent(eventBuilder.build());

            throw e;
        }
    }

    private static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }

        return map;
    }

    private Map<String, Object> extractMethodArgs(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Map<String, Object> argsMap = new LinkedHashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            argsMap.put(parameterNames[i], args[i]);
        }
        return argsMap;
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }

    private String getCurrentUserRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null) {
            return auth.getAuthorities().iterator().next().getAuthority();
        }
        return null;
    }
}
