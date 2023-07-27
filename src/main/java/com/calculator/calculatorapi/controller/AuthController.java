package com.calculator.calculatorapi.controller;

import com.calculator.calculatorapi.dto.StandardResponseDto;
import com.calculator.calculatorapi.dto.authentication.SignInResponse;
import com.calculator.calculatorapi.dto.authentication.LoginRequest;
import com.calculator.calculatorapi.dto.authentication.SignupRequest;
import com.calculator.calculatorapi.dto.user.UserDto;
import com.calculator.calculatorapi.service.security.AuthenticationService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*",
        maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signin")
    public ResponseEntity<StandardResponseDto<SignInResponse>> authenticateUser(
            @Valid @RequestBody final LoginRequest loginRequest
    ) {
        final StandardResponseDto<SignInResponse> standardResponseDto = new StandardResponseDto<>(
                authenticationService.authenticateUser(loginRequest),
                "User authenticated successfully",
                null
        );
        return ResponseEntity.ok().body(standardResponseDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<StandardResponseDto<UserDto>> registerUser(
            @Valid @RequestBody final SignupRequest signUpRequest
    ) {
        final StandardResponseDto<UserDto> standardResponseDto = new StandardResponseDto<>(
                authenticationService.registerUser(signUpRequest),
                "User created successfully",
                null
        );
        return ResponseEntity.ok().body(standardResponseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<StandardResponseDto<UserDto>> logUserOut(
    ) {
        final StandardResponseDto<UserDto> standardResponseDto = new StandardResponseDto<>(
                null,
                "User logged out successfully",
                null
        );
        return ResponseEntity.ok().body(standardResponseDto);
    }
}
