package com.example.tickets.controller;

import com.example.tickets.dto.AuthRequestDto;
import com.example.tickets.dto.GrantRoleDto;
import com.example.tickets.dto.User;
import com.example.tickets.security.SecurityService;
import com.example.tickets.security.dto.RefreshTokenRequest;
import com.example.tickets.security.dto.TokenInfo;
import com.example.tickets.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;


@Tag(name = "auth", description = "Auth API")
@RestController
@Validated
@RequestMapping("/auth")
public class AuthController {

    private UserService userService;

    private SecurityService securityService;

    @Operation(summary = "Register a user", tags = "register")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Register a user",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class))
                    })
    })
    @PostMapping("/register")
    public Mono<User> register(@Valid User user) {
        return userService.addUser(user);
    }

    @Operation(summary = "Grant role", tags = "grantMe",
            parameters = @Parameter(name = "grantRoleDto",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = GrantRoleDto.class))
                    }))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Grant role to the current user",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class))
                    })
    })
    @PostMapping(value = "/grantMe")
    public Mono<User> grantRole(@Valid @RequestBody GrantRoleDto grantRoleDto, Mono<Principal> monoPrincipal) {
        return monoPrincipal.flatMap(principal -> userService.grantRole(principal.getName(), grantRoleDto.getRoleName()));
    }

    @Operation(summary = "Login", tags = "login",
            parameters = @Parameter(name = "authRequestDto",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthRequestDto.class))
                    }))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Get JWT token",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenInfo.class))
                    })
    })
    @PostMapping("/login")
    public Mono<ResponseEntity<TokenInfo>> login(@Valid AuthRequestDto authRequestDto) {
        return securityService.authenticate(authRequestDto.getLogin(), authRequestDto.getPassword())
                .flatMap(tokenInfo -> Mono.just(ResponseEntity.ok(tokenInfo)));
    }

    @Operation(summary = "Refresh token", tags = "refreshToken",
            parameters = @Parameter(name = "tokenRequest",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RefreshTokenRequest.class))
                    }))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Get new JWT token",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenInfo.class))
                    })
    })
    @PostMapping("/refreshToken")
    public Mono<ResponseEntity<TokenInfo>> refreshToken(@Valid RefreshTokenRequest tokenRequest, Mono<Principal> principalMono) {
        return principalMono.flatMap(principal -> securityService.refreshToken(tokenRequest.getRefreshToken(), principal.getName()))
                .map(ResponseEntity::ok);
    }

    @Autowired
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Autowired
    public void setClientService(UserService userService) {
        this.userService = userService;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleConstraintViolationException(ConstraintViolationException e) {
        return "Validation error: " + e.getMessage();
    }
}
