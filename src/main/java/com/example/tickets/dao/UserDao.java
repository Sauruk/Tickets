package com.example.tickets.dao;

import com.example.tickets.dto.Role;
import com.example.tickets.dto.User;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.db.postgres.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.jooq.db.postgres.Tables.*;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;

@Service
public class UserDao extends AbstractDao implements RecordMapper<Record, User> {

    private static final String ROLES_ALIAS = "roles";

    private RecordMapper<Record, Role> roleRecordMapper;

    public Mono<User> persist(User user) {
        return Mono.justOrEmpty(dsl
                .insertInto(USERS)
                .set(USERS.LOGIN, user.getLogin())
                .set(USERS.PASSWORD, user.getPassword())
                .set(USERS.USERNAME, user.getUsername())
                .returning(USERS.fields()).fetchOneInto(User.class));
    }

    public Mono<User> addRole(String login, String roleName) {
        return Mono.justOrEmpty(dsl.selectFrom(Tables.ROLE).where(Tables.ROLE.NAME.eq(roleName))
                        .fetchOptionalInto(Role.class))
                .flatMap(role -> Mono.justOrEmpty(dsl.insertInto(Tables.USERS_ROLE)
                        .set(Tables.USERS_ROLE.USER_LOGIN, login)
                        .set(Tables.USERS_ROLE.ROLE_ID, role.getId()).returning().fetchOne()))
                .flatMap(
                        roleRecord -> Mono.justOrEmpty(findUserByLogin(login))
                )
                .switchIfEmpty(Mono.error(new RuntimeException("No such role")));
    }

    public Optional<User> findUserByLogin(String login) {
        return dsl
                .select(USERS.asterisk(),
                        multiset(
                                select(ROLE.fields())
                                        .from(ROLE.join(USERS_ROLE).on(ROLE.ID.eq(USERS_ROLE.ROLE_ID)))
                                        .where(USERS_ROLE.USER_LOGIN.eq(USERS.LOGIN))
                        ).as(ROLES_ALIAS)
                ).from(USERS)
                .where(USERS.LOGIN.eq(login))
                .fetchOptional(this);
    }

    public boolean isExist(String login) {
        return dsl.fetchExists(USERS, USERS.LOGIN.eq(login));
    }

    @Override
    public User map(Record record) {
        User user = new User();
        user.setLogin(record.getValue(USERS.LOGIN));
        user.setUsername(record.getValue(USERS.USERNAME));
        user.setPassword(record.getValue(USERS.PASSWORD));
        for (Record roleRecord : record.getValue(ROLES_ALIAS, Record[].class)) {
            user.getRoles().add(roleRecordMapper.map(roleRecord));
        }
        return user;
    }


    @Autowired
    public void setRoleRecordMapper(RecordMapper<Record, Role> roleRecordMapper) {
        this.roleRecordMapper = roleRecordMapper;
    }
}
