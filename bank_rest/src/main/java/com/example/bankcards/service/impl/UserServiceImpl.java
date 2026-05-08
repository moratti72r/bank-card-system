package com.example.bankcards.service.impl;

import com.example.bankcards.dto.*;
import com.example.bankcards.dto.mapper.UserMapper;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessLogicException;
import com.example.bankcards.exception.NotFoundEntityException;
import com.example.bankcards.exception.UniqueValueException;
import com.example.bankcards.kafka.KafkaEventSender;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.JwtUtils;
import com.example.common.message.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.example.bankcards.exception.ExceptionMessages.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Lazy
    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    @Override
    @KafkaEventSender(type = EventType.USER_LOGGED_IN)
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundEntityException("Пользователь с почтой + " + email + " не найден"));
        Role role = user.getRoles().iterator().next();
        String jwt = jwtUtils.generateJwtToken(email, role.getName().name());

        return new JwtResponse(jwt, email, role.getName().name());

    }

    @Override
    @KafkaEventSender(type = EventType.USER_LOGOUT, hasResult = false)
    public void logout(String token) {
        jwtUtils.blacklistToken(token);
    }

    @Override
    @KafkaEventSender(type = EventType.USER_REGISTERED)
    public UserResponseDto register(UserRequestDto userDto) {

        String encodedPassowrd = passwordEncoder.encode(userDto.getPassword());
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new NotFoundEntityException("Роль + " + Role.RoleName.ROLE_USER + " не найдена"));

        User user = UserMapper.userRequestToUser(userDto, encodedPassowrd, Set.of(userRole));

        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new UniqueValueException("Такой номер телефона уже существует");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UniqueValueException("Такой почтовый адрес уже существует");
        }
        User saveUser = userRepository.save(user);
        UserResponseDto result = UserMapper.userToUserResponseDto(user);

        return result;
    }

    @Override
    @Transactional
    @KafkaEventSender(type = EventType.USER_TO_ADMIN, hasResult = false)
    public void userToAdmin(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundEntityException(String.format(USER_NOT_FOUND, id)));
        Role role = roleRepository.findByName(Role.RoleName.ROLE_ADMIN).orElseThrow(() -> new NotFoundEntityException("Роль отсутствует"));
        user.setRoles(new HashSet<>(Set.of(role)));
        userRepository.save(user);
    }

    @Override
    @KafkaEventSender(type = EventType.USER_DELETED, hasResult = false)
    public void deleteUserById(UUID id) {
        if (!userRepository.existsById(id)) throw new NotFoundEntityException(String.format(USER_NOT_FOUND, id));
        userRepository.deleteById(id);
    }

    @Override
    @KafkaEventSender(type = EventType.USER_UPDATED, hasResult = false)
    public void updateUserInfo(UserUpdateRequestDto updateDto, UUID userId) {
        if (userRepository.existsByPhoneNumber(updateDto.getPhoneNumber()))
            throw new UniqueValueException("Такой номер телефона уже существует");
        if (userRepository.existsByEmail(updateDto.getEmail()))
            throw new UniqueValueException("Такой почтовый адрес уже существует");

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundEntityException(String.format(USER_NOT_FOUND, userId)));

        if (!passwordEncoder.matches(updateDto.getOldPassword(), user.getPassword()))
            throw new BusinessLogicException("Неверный пароль");

        updateDto.setOldPassword(passwordEncoder.encode(updateDto.getOldPassword()));
        updateDto.setNewPassword(updateDto.getNewPassword() != null ? passwordEncoder.encode(updateDto.getNewPassword()) : null);
        UserMapper.userUpdateToUser(user, updateDto);
        userRepository.save(user);

    }
}
