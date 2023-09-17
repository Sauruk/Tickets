package com.example.tickets.dao;

import com.example.tickets.dto.Route;
import com.example.tickets.dto.TransportCompany;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.db.postgres.Tables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class RouteDao extends AbstractDao implements RecordMapper<Record, Route> {

    private RecordMapper<Record, TransportCompany> transportCompanyRecordMapper;

    public Collection<Route> findAll() {
        return dsl.selectFrom(Tables.ROUTE).fetchInto(Route.class);
    }

    @Override
    public Route map(Record record) {
        Route route = new Route();
        route.setId(record.getValue(Tables.ROUTE.ID));
        route.setArrival(record.getValue(Tables.ROUTE.ARRIVAL));
        route.setDepartment(record.getValue(Tables.ROUTE.DEPARTMENT));
        route.setTravelTimeMin(record.getValue(Tables.ROUTE.TRAVEL_TIME_MIN));
        route.setTransportCompany(transportCompanyRecordMapper.map(record));
        return route;
    }

    @Autowired
    public void setTransportCompanyRecordMapper(RecordMapper<Record, TransportCompany> transportCompanyRecordMapper) {
        this.transportCompanyRecordMapper = transportCompanyRecordMapper;
    }
}


