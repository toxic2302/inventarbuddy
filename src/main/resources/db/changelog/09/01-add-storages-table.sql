-- liquibase formatted sql

-- changeset floriankolb:1787300000000-1
CREATE TABLE storages
(
    id          UUID                           NOT NULL,
    createdAt   TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    createdBy   VARCHAR(255)                   NOT NULL,
    modifiedAt  TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modifiedBy  VARCHAR(255)                   NOT NULL,
    name        VARCHAR(255),
    description VARCHAR(255),
    user_id     UUID,
    parent_id   UUID,
    CONSTRAINT pk_storages PRIMARY KEY (id)
);

-- changeset floriankolb:1787300000000-2
ALTER TABLE storages ADD CONSTRAINT FK_STORAGES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset floriankolb:1787300000000-3
ALTER TABLE storages ADD CONSTRAINT FK_STORAGES_ON_PARENT FOREIGN KEY (parent_id) REFERENCES storages (id);
