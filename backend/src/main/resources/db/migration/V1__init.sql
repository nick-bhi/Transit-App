create table users (
    id uuid primary key,
    email varchar(255) unique not null,
    password_hash varchar(255) not null,
    mfa_enabled boolean not null default true
);

create table stops (
    id text primary key,
    name text not null,
    lat double precision not null,
    lon double precision not null
);

create table routes (
    id text primary key,
    short_name text,
    long_name text
);
