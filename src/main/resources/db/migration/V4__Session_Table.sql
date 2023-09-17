CREATE TABLE SESSION
(
    id    SERIAL PRIMARY KEY,
    login varchar(50) NOT NULL REFERENCES USERS (login) ON DELETE CASCADE,
    refreshToken varchar NOT NULL,
    expireDate timestamp NOT NULL
);

