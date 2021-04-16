create sequence hibernate_sequence start 1 increment 1;
create sequence appliance_id_seq start 1 increment 1 minvalue 1 maxvalue 9223372036854775807 cache 1;
create sequence program_id_seq start 1 increment 1 minvalue 1 maxvalue 9223372036854775807 cache 1;
create sequence wash_id_seq start 1 increment 1 minvalue 1 maxvalue 9223372036854775807 cache 1;

create table if not exists appliance(
    id int8 primary key,
    name varchar(30) not null unique,
    status varchar(30)
);

create table if not exists program(
    id int8 primary key,
    name varchar(30) not null,
    duration_in_minutes int8 not null,
    temperature integer not null,
    spin_speed integer not null
);

create table if not exists wash(
    id int8 primary key,
    appliance_id int8 not null,
    program_id int8 not null,
    start_time timestamp not null,
    finish_time timestamp,
    constraint wash_appliance_id foreign key (appliance_id) references appliance(id),
    constraint wash_program_id foreign key (program_id) references program(id)
)