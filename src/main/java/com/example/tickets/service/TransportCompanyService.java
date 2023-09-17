package com.example.tickets.service;

import com.example.tickets.dao.TransportCompanyDao;
import com.example.tickets.dto.TransportCompany;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TransportCompanyService {

    private final TransportCompanyDao transportCompanyDao;

    public Mono<TransportCompany> addNewCompany(TransportCompany transportCompany) {
        return transportCompanyDao.persist(transportCompany);
    }
}
