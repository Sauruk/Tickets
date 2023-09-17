package com.example.tickets.security.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RefreshToken {

    private long id;
    private String login;
    private String refreshToken;
    private LocalDateTime expireDate;

}
