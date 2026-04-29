package com.meetingroom.api.service;

import com.meetingroom.api.dto.LoginRequest;
import com.meetingroom.api.dto.LoginResponse;
import com.meetingroom.api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public LoginResponse authenticate(LoginRequest request) {
        log.info("Tentativa de login para usuário: {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        log.info("Login bem-sucedido para usuário: {}", request.getUsername());

        return LoginResponse.builder()
                .token(token)
                .username(userDetails.getUsername())
                .type("Bearer")
                .expiresIn(jwtUtil.getExpiration())
                .build();
    }
}
