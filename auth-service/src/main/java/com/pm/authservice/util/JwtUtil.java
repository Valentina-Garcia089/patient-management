package com.pm.authservice.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Date;

// esta es la clase que se encargará de generar y validar los tokens JWT
@Component
public class JwtUtil {

    private final Key secretKey;

    // Esto viene del application.properties
    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }



    public String generateToken(String email, String role) {
        // Genera un token JWT con el email y el rol del usuario, con una validez de 10 horas
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) //10 horas de validez
                .signWith(secretKey)
                .compact();
    }


    public void validateToken(String token) {
        try{
            // esto valida haciendo uso de la clave secreta que se usó para firmar el token
            Jwts.parser().verifyWith((SecretKey) secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (JwtException e){
            throw new JwtException("Invalid JWT token");
        }
    }
}
