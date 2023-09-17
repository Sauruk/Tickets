package com.example.tickets.dao;

import com.example.tickets.dto.BuyTicketDto;
import com.example.tickets.dto.Route;
import com.example.tickets.dto.Ticket;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.db.postgres.Tables;
import org.jooq.db.postgres.tables.records.TicketRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.db.postgres.tables.Ticket.TICKET;

@Service
public class TicketDao extends AbstractDao implements RecordMapper<Record, Ticket> {

    private RecordMapper<Record, Route> routeRecordMapper;

    public Mono<Ticket> buyTicket(String login, BuyTicketDto dto) {
        return Mono.justOrEmpty(dsl.selectFrom(Tables.USERS_TICKET)
                        .where(Tables.USERS_TICKET.TICKET_ID.eq(dto.getTicketId())).fetchOptional()
                        .isEmpty()).
                flatMap(exist -> {
                    if (exist) {
                        return Mono.error(new RuntimeException("Ticket already sold"));
                    } else {
                        return Mono.justOrEmpty(dsl.insertInto(Tables.USERS_TICKET)
                                        .set(Tables.USERS_TICKET.TICKET_ID, dto.getTicketId())
                                        .set(Tables.USERS_TICKET.USER_LOGIN, login).execute())
                                .filter(result -> result > 0)
                                .flatMap(r -> findTicketById(dto.getTicketId()));
                    }
                });
    }

    public Mono<Ticket> findTicketById(int id) {
        return Mono.justOrEmpty(getTicketSelect()
                .where(Tables.TICKET.ID.eq(id))
                .fetchOne(this));
    }

    public Mono<List<Ticket>> findClientTickets(String login) {
        return Mono.just(getTicketSelect()
                .leftJoin(Tables.USERS_TICKET)
                .on(TICKET.ID.eq(Tables.USERS_TICKET.TICKET_ID))
                .where(Tables.USERS_TICKET.USER_LOGIN.eq(login))
                .fetch(this));
    }

    private SelectOnConditionStep<Record> getTicketSelect() {
        return dsl.select()
                .from(TICKET)
                .join(Tables.ROUTE)
                .on(TICKET.ROUTE.eq(Tables.ROUTE.ID))
                .join(Tables.TRANSPORT_COMPANY)
                .on(Tables.ROUTE.TRANSPORT_COMPANY_ID.eq(Tables.TRANSPORT_COMPANY.ID));
    }

    public Mono<List<Ticket>> findAvailableTickets(LocalDateTime date, String departure, String destination, String companyName) {
        SelectConditionStep<Record> step = dsl.select()
                .from(TICKET)
                .leftJoin(Tables.USERS_TICKET)
                .on(TICKET.ID.eq(Tables.USERS_TICKET.TICKET_ID))
                .join(Tables.ROUTE)
                .on(TICKET.ROUTE.eq(Tables.ROUTE.ID))
                .join(Tables.TRANSPORT_COMPANY)
                .on(Tables.ROUTE.TRANSPORT_COMPANY_ID.eq(Tables.TRANSPORT_COMPANY.ID))
                .where(Tables.USERS_TICKET.USER_LOGIN.isNull());

        if (date != null) {
            step = step.and(TICKET.DATE.eq(date));
        }
        if (departure != null) {
            step = step.and(Tables.ROUTE.DEPARTMENT.containsIgnoreCase(departure));
        }
        if (destination != null) {
            step = step.and(Tables.ROUTE.ARRIVAL.containsIgnoreCase(destination));
        }
        if (companyName != null) {
            step = step.and(Tables.TRANSPORT_COMPANY.NAME.containsIgnoreCase(companyName));
        }

        return Mono.just(step.fetch(this));
    }

    public void persist(List<TicketRecord> tickets) {
        dsl.batchInsert(tickets).execute();
    }

    @Autowired
    public void setRouteRecordMapper(RecordMapper<Record, Route> routeRecordMapper) {
        this.routeRecordMapper = routeRecordMapper;
    }

    @Override
    public Ticket map(Record record) {
        Ticket ticket = new Ticket();
        ticket.setDate(record.getValue(Tables.TICKET.DATE));
        ticket.setPrice(record.getValue(Tables.TICKET.PRICE));
        ticket.setSeat(record.getValue(Tables.TICKET.SEAT));
        ticket.setRoute(routeRecordMapper.map(record));
        return ticket;
    }
}
