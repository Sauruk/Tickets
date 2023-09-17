INSERT INTO ROLE (name)
VALUES ('ADMIN'),
       ('CUSTOMER');

INSERT INTO transport_company (name, phone)
VALUES ('Доедем и точка', 89081231234124),
       ('Шустро со вкусом', 890855121244),
       ('Тише едешь дольше в пробке', 890723452356),
       ('Эх, прокачу!', 89085555555);


INSERT INTO ROUTE (department, arrival, travel_time_min, transport_company_id)
VALUES ('От сюда', 'Туда', 5, 1),
       ('Пролетарская', 'Муромская', 55, 2),
       ('Пролетарская', 'Муромская', 155, 3),
       ('Москва', 'Воронеж', 180, 4);