package com.example.tickets.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo {
    private String login;
    private String token;
    private String refreshToken;
    private Date issuedAt;
    private Date expiresAt;
}