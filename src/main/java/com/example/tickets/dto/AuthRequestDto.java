package com.example.tickets.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthRequestDto {
    @NotNull
    private String login;
    @NotNull
    private String password;
}