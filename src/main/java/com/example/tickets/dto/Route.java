/*
 * This file is generated by jOOQ.
 */
package com.example.tickets.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Route implements Serializable {

    private Integer id;
    private String department;
    private String arrival;
    private Integer travelTimeMin;
    private TransportCompany transportCompany;

}