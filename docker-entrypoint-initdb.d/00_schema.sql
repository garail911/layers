-- DDL (Data Definition Language)
-- DML (Data Manipulation Language) -> Insert, Update, Delete
-- DRL/DQL (Data Query Language) -> Select
-- TCL (Transaction Control Language) -> Begin/Rollback/Commit
-- DCL (Data Control Language) -> GRANT/REVOKE

-- rows с фикс.кол-вом столбцов (и их раположением)
-- 1 столбец - 1 значение ([], JSON, Set, ...)
-- AUTO_INCREMENT, нет, есть SERIAL
-- CHAR(255) -> 255, VARCHAR(255) -> ..., TEXT!
--    this.login = login;
--    this.password = password;
--    this.name = name;
--    this.secret = secret;
--    this.roles = roles;
--    this.removed = removed;
--    this.created = created;
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    login TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    name TEXT NOT NULL,
    secret TEXT NOT NULL,
    roles TEXT[] NOT NULL DEFAULT '{}',
    removed BOOLEAN NOT NULL DEFAULT false,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);