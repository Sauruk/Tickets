-- USER

CREATE TABLE USERS
(
    username varchar(200),
    login    varchar(50) NOT NULL PRIMARY KEY,
    password varchar     NOT NULL
);

CREATE TABLE ROLE
(
    id    SERIAL PRIMARY KEY,
    name    varchar(50) NOT NULL
);

-- TRANSPORT_COMPANY

CREATE TABLE TRANSPORT_COMPANY
(
    id    SERIAL PRIMARY KEY,
    name  varchar(200),
    phone bigint
);

-- ROUTE

CREATE TABLE ROUTE
(
    id                   SERIAL PRIMARY KEY,
    department           varchar(200) NOT NULL,
    arrival              varchar(200) NOT NULL,
    travel_time_min      integer,
    transport_company_id integer references TRANSPORT_COMPANY (id)
);

CREATE TABLE TICKET
(
    id    SERIAL PRIMARY KEY,
    seat  integer,
    price float,
    date  timestamp,
    route integer references ROUTE (id)
);
