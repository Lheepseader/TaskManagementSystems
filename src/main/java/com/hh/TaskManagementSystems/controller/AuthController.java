package com.hh.TaskManagementSystems.controller;

import com.hh.TaskManagementSystems.dto.AuthRequestDto;
import com.hh.TaskManagementSystems.dto.AuthResponseDto;
import com.hh.TaskManagementSystems.dto.RegistrationRequestDto;
import com.hh.TaskManagementSystems.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "Регистрация пользователя")
    public AuthResponseDto registration(@RequestBody @Valid RegistrationRequestDto request) {
        return authenticationService.registration(request);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/login")
    public AuthResponseDto authorization(@RequestBody @Valid AuthRequestDto request) {
        return authenticationService.authorization(request);
    }
}