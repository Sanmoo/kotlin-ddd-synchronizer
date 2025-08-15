create table outbox (
    id varchar(255) primary key,
    event_body varchar,
    created_at timestamp
);

create table resource_a (
    id varchar(255) primary key,
    name varchar(255)
);