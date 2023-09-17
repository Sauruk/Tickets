package com.example.tickets.security;

import com.example.tickets.dto.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;
import java.util.stream.Collectors;


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(useAuthorizationManager = true)
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secret;

    private final String[] publicRoutes = {"/auth/login", "auth/register"};

    static final String ROLES_CLAIM_NAME = "roles";

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager authenticationManager) {
        return http
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec
                        .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(source -> {
                            List<String> roles = source.getClaim(ROLES_CLAIM_NAME);
                            return Mono.just(new JwtAuthenticationToken(source, roles.stream().map(Role::new).collect(Collectors.toList())));
                        })))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec ->
                        authorizeExchangeSpec.pathMatchers(HttpMethod.OPTIONS).permitAll()
                                .pathMatchers(publicRoutes).permitAll()
                                .pathMatchers("/admin", "/admin/*").hasAuthority("ADMIN")
                                .anyExchange().authenticated())
                .exceptionHandling(exceptionHandlingSpec -> {
                    exceptionHandlingSpec.accessDeniedHandler((swe, e) -> Mono.fromRunnable(() ->
                            swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)));
                    exceptionHandlingSpec.authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() ->
                            swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)));
                })
                // .addFilterAt(bearerAuthenticationFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveJwtDecoder jwtDecoder) {
        return new JwtReactiveAuthenticationManager(jwtDecoder);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveJwtDecoder nimbusJwtDecoder() {
        return NimbusReactiveJwtDecoder
                .withSecretKey(new SecretKeySpec(secret.getBytes(), "HMACSHA256"))
                .build();
    }

    private AuthenticationWebFilter bearerAuthenticationFilter(ReactiveAuthenticationManager authenticationManager) {
        AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter(authenticationManager);
        bearerAuthenticationFilter.setServerAuthenticationConverter(new BearerTokenServerAuthenticationConverter(new JwtHandler(secret)));
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));
        return bearerAuthenticationFilter;
    }
}
