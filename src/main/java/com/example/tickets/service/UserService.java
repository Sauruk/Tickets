package com.example.tickets.service;

import com.example.tickets.dao.UserDao;
import com.example.tickets.dto.Role;
import com.example.tickets.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Transactional
public class UserService implements ReactiveUserDetailsService {

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;

    public Mono<User> addUser(User user) {
        return Mono.just(user)
                .map(this::encodePassword)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(userDao::persist);
    }

    public Mono<User> grantRole(String login, String roleName) {
        return findByLogin(login).filter(user -> user.getRoles().stream().map(Role::getName)
                        .anyMatch(userRole -> userRole.equals(roleName)))
                .or(userDao.addRole(login, roleName));
    }

    public Mono<User> findByLogin(String login) {
        return Mono.justOrEmpty(userDao.findUserByLogin(login));
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userDao.findUserByLogin(username).map(UserDetails.class::cast)
                .map(Mono::just)
                .orElse(Mono.empty());
    }

    private User encodePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return user;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
