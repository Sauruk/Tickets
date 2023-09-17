package com.example.tickets.security;

import com.example.tickets.dto.Role;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class UserAuthenticationBearer {
    public static Mono<Authentication> create(JwtHandler.VerificationResult verificationResult) {
        Claims claims = verificationResult.claims;
        String subject = claims.getSubject();

        List<Role> roles = claims.get("role", ArrayList.class);

        return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(subject, null, roles));
    }
}
