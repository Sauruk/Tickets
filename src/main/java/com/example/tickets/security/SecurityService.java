package com.example.tickets.security;

import com.example.tickets.dto.User;
import com.example.tickets.security.dto.TokenInfo;
import com.example.tickets.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SecurityService {
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    private RefreshTokenService refreshTokenService;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long defaultExpirationTimeInSecondsConf;
    @Value("${jwt.issuer}")
    private String issuer;

    private TokenInfo generateToken(User user, String refreshToken) {
        Map<String, Object> claims = new HashMap<>() {{
            put(SecurityConfig.ROLES_CLAIM_NAME, user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
        }};
        return generateToken(claims, user.getLogin(), refreshToken);
    }

    private TokenInfo generateToken(Map<String, Object> claims,
                                    String subject, String refreshToken) {
        Date createdDate = new Date();
        Long expirationTimeInMillis = defaultExpirationTimeInSecondsConf * 1000L;
        Date expirationDate = new Date(createdDate.getTime() + expirationTimeInMillis);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setId(UUID.randomUUID().toString())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();

        return TokenInfo.builder()
                .token(token)
                .issuedAt(createdDate)
                .expiresAt(expirationDate)
                .refreshToken(refreshToken)
                .build();
    }

    public Mono<TokenInfo> refreshToken(String token, String login) {
        return userService.findByLogin(login).
                flatMap(user -> refreshTokenService.findByToken(token)
                        .flatMap(refreshTokenService::verifyExpiration)
                        .map(refreshToken -> generateToken(user, refreshToken.getRefreshToken())));
    }

    public Mono<TokenInfo> authenticate(String login, String password) {

        return userService.findByLogin(login)
                .flatMap(user -> {
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.error(new AuthException("Invalid password"));
                    }
                    return refreshTokenService.createRefreshToken(login)
                            .map(refreshToken -> generateToken(user, refreshToken.getRefreshToken()));
                })
                .switchIfEmpty(Mono.error(new AuthException("Invalid user, " + login + " is not registered.")));
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setRefreshTokenService(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }
}