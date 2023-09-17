package com.example.tickets.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BuyTicketDto {

    @NotNull
    private Integer ticketId;

}
