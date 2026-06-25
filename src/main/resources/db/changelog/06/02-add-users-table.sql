-- liquibase formatted sql

-- changeset floriankolb:2
CREATE TABLE users
(
    id            UUID                        NOT NULL,
    createdAt     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    createdBy     VARCHAR(255)                NOT NULL,
    modifiedAt    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modifiedBy    VARCHAR(255)                NOT NULL,
    keycloakId    VARCHAR(255)                NOT NULL,
    email         VARCHAR(255),
    firstName     VARCHAR(255),
    lastName      VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset floriankolb:3
ALTER TABLE users
    ADD CONSTRAINT uc_users_keycloakid UNIQUE (keycloakId);
