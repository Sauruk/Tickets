package com.example.tickets.security;

import com.example.tickets.security.dao.SessionDao;
import com.example.tickets.security.dto.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${refreshToken.expiration}")
    private Long refreshTokenDurationSeconds;

    private SessionDao sessionDao;

    public Mono<RefreshToken> findByToken(String token) {
        return sessionDao.findByToken(token);
    }

    public Mono<RefreshToken> createRefreshToken(String login) {
        return sessionDao.saveSession(login,
                LocalDateTime.now().plusSeconds(refreshTokenDurationSeconds),
                UUID.randomUUID().toString());
    }

    public Mono<RefreshToken> verifyExpiration(RefreshToken token) {
        if (token.getExpireDate().compareTo(LocalDateTime.now()) < 0) {
            return sessionDao.deleteSession(token.getLogin())
                    .flatMap(deleted -> Mono.error(new AuthException("Refresh token was expired")));
        }
        return Mono.just(token);
    }

    @Autowired
    public void setSessionDao(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }
}
