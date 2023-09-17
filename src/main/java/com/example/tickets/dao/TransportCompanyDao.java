package com.example.tickets.dao;

import com.example.tickets.dto.TransportCompany;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.db.postgres.Tables;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.jooq.db.postgres.Tables.TRANSPORT_COMPANY;
import static org.jooq.db.postgres.Tables.USERS;

@Service
public class TransportCompanyDao extends AbstractDao implements RecordMapper<Record, TransportCompany> {

    public Mono<TransportCompany> persist(TransportCompany transportCompany) {
        return Mono.justOrEmpty(dsl
                .insertInto(TRANSPORT_COMPANY)
                .set(TRANSPORT_COMPANY.NAME, transportCompany.getName())
                .set(TRANSPORT_COMPANY.PHONE, transportCompany.getPhone())
                .returning(USERS.fields()).fetchOne(this));
    }

    @Override
    public TransportCompany map(Record record) {
        TransportCompany company = new TransportCompany();
        company.setId(record.getValue(Tables.TRANSPORT_COMPANY.ID));
        company.setName(record.getValue(Tables.TRANSPORT_COMPANY.NAME));
        company.setPhone(record.getValue(Tables.TRANSPORT_COMPANY.PHONE));
        return company;
    }
}
