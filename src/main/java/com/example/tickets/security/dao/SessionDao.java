package com.example.tickets.security.dao;

import com.example.tickets.dao.AbstractDao;
import com.example.tickets.security.dto.RefreshToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.jooq.db.postgres.Tables.SESSION;

@Service
public class SessionDao extends AbstractDao {

    public Mono<RefreshToken> findByToken(String token) {
        return Mono.justOrEmpty(dsl.selectFrom(SESSION)
                .where(SESSION.REFRESHTOKEN.eq(token))
                .fetchOneInto(RefreshToken.class));
    }

    public Mono<RefreshToken> saveSession(String login, LocalDateTime expireDate, String token) {
        return Mono.just(dsl.fetchExists(SESSION, SESSION.LOGIN.eq(login)))
                .map(exist -> exist ? update(login, expireDate, token) : persist(login, expireDate, token));
    }

    public Mono<Boolean> deleteSession(String login) {
        return Mono.justOrEmpty(dsl.deleteFrom(SESSION)
                .where(SESSION.LOGIN.eq(login)).execute()).map(result -> result > 0);
    }


    private RefreshToken persist(String login, LocalDateTime expireDate, String token) {
        return dsl.insertInto(SESSION)
                .set(SESSION.LOGIN, login)
                .set(SESSION.REFRESHTOKEN, token)
                .set(SESSION.EXPIREDATE, expireDate)
                .returning(SESSION.asterisk()).fetchOneInto(RefreshToken.class);
    }

    private RefreshToken update(String login, LocalDateTime expireDate, String token) {
        return dsl.update(SESSION)
                .set(SESSION.LOGIN, login)
                .set(SESSION.REFRESHTOKEN, token)
                .set(SESSION.EXPIREDATE, expireDate)
                .returning(SESSION.asterisk()).fetchOneInto(RefreshToken.class);
    }
}
