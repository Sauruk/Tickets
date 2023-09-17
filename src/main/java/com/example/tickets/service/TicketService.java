package com.example.tickets.service;

import com.example.tickets.dao.RouteDao;
import com.example.tickets.dao.TicketDao;
import com.example.tickets.dto.BuyTicketDto;
import com.example.tickets.dto.Ticket;
import org.jooq.db.postgres.tables.records.TicketRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class TicketService {

    private TicketDao ticketDao;
    private RouteDao routeDao;

    public Mono<List<Ticket>> getClientTickets(String login) {
        return ticketDao.findClientTickets(login);
    }

    public Mono<Ticket> buyTicket(String login, BuyTicketDto dto) {
        return Mono.just(dto)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(ticketDto -> ticketDao.buyTicket(login, ticketDto));
    }

    public Mono<List<Ticket>> getAvailableTickets(LocalDateTime date, String depart, String destination, String companyName) {
        return ticketDao.findAvailableTickets(date, depart, destination, companyName);
    }

    @Autowired
    public void setTicketDao(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    @Autowired
    public void setRouteDao(RouteDao routeDao) {
        this.routeDao = routeDao;
    }

    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(fixedRate = 100000)
    public void publishNewTickets() {
        LocalDateTime nextMidnight = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        ticketDao.persist(routeDao.findAll().stream()
                .flatMap(route -> generateTicketsForRoute(route.getId(), nextMidnight).stream())
                .collect(Collectors.toList()));
    }

    private List<TicketRecord> generateTicketsForRoute(int routeId, LocalDateTime midnight) {
        List<TicketRecord> records = new ArrayList<>();
        for (int hour = 0; hour < 23; hour++) {
            for (int seat = 1; seat <= 20; seat++) {
                TicketRecord record = new TicketRecord();
                record.setSeat(seat);
                record.setRoute(routeId);
                record.setDate(midnight.withHour(hour));
                record.setPrice(hour < 9 ? 200 : 150.5);
                records.add(record);
            }
        }
        return records;
    }
}
