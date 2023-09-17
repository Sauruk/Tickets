package com.example.tickets.controller;

import com.example.tickets.dto.TransportCompany;
import com.example.tickets.service.TransportCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TransportCompanyService transportCompanyService;

    @PostMapping("/createCompany")
    public Mono<TransportCompany> createTransportCompany(TransportCompany transportCompany) {
        return transportCompanyService.addNewCompany(transportCompany);
    }

}
