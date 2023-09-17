package com.example.tickets.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer seat;
    private Double price;
    private LocalDateTime date;
    private Route route;

}
