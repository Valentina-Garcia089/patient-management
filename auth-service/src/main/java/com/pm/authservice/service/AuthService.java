package com.pm.authservice.service;


import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }


    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO){
        // si no existen el usuario o la contraseña es incorrecta, se devuelve un Optional vacio
        Optional<String> token = userService
                .findByEmail(loginRequestDTO.email())
                .filter(u-> passwordEncoder.matches(loginRequestDTO.password(), u.getPassword())) // u = resultado de findByEmail
                .map(u-> jwtUtil.generateToken(u.getEmail(), u.getRole())); // solo si el usuario existe y la contraseña es correcta, se genera el token

        return token;
    }



    public boolean validateToken(String token){
        try{
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e){
            return false;
        }
    }
}
