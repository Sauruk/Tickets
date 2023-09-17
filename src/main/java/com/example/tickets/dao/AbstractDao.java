package com.example.tickets.dao;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDao {

   protected DSLContext dsl;

    @Autowired
    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

}
