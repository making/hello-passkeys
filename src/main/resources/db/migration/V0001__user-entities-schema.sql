-- https://github.com/spring-projects/spring-security/blob/main/web/src/main/resources/org/springframework/security/user-entities-schema.sql
create table user_entities (
    id varchar(1000) not null,
    name varchar(100) not null,
    display_name varchar(200),
    primary key (id)
);