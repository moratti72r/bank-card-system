package com.example.bankcards.service;

import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("userSecurityService")
@RequiredArgsConstructor
public class UserSecurityService {

    private final CardRepository cardRepository;

    public boolean ownsCard(UUID id) {
        UUID userId = getCurrentUserId();
        if (userId == null || id == null) return false;

        return cardRepository.findById(id).map(card -> card.getUser().getId().equals(userId)).orElse(false);
    }

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails userDetails) {
                return userDetails.getId();
            }
        }
        return null;
    }
}
