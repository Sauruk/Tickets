package com.example.tickets.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;
}
