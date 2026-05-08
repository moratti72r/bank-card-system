package org.example.audit.auditor.security;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.audit.auditor.util.JwtExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class SecurityAspect {

    @Autowired
    private JwtExtractor jwtExtractor;

    @Before("@annotation(onlyAdmin)")
    public void checkRole(JoinPoint jp, OnlyAdmin onlyAdmin) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String role = getTokenRole(request);

        System.out.println("========================ROLE ======" + role);
        if (!role.equals("ROLE_ADMIN")) throw new RuntimeException("FORBIDDEN");

    }


    private String getTokenRole(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) throw new RuntimeException("UNAUTHORIZED");

        if (authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);

            return jwtExtractor.getRoleFromJwtToken(token);

        }else throw new RuntimeException("FORBIDDEN");
    }

}
