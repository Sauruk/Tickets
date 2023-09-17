package com.example.tickets.dao;

import com.example.tickets.dto.Role;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.db.postgres.Tables;
import org.springframework.stereotype.Service;

import static org.jooq.db.postgres.Tables.ROLE;

@Service
public class RoleDao extends AbstractDao implements RecordMapper<Record, Role> {

    public boolean isExist(String roleName) {
        return dsl.fetchExists(ROLE, ROLE.NAME.eq(roleName));
    }

    @Override
    public Role map(Record record) {
        Role role = new Role();
        role.setId(record.getValue(Tables.ROLE.ID));
        role.setName(record.getValue(Tables.ROLE.NAME));
        return role;
    }
}
