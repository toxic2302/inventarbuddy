-- liquibase formatted sql

-- changeset floriankolb:1787205479277-3
CREATE TABLE categories
(
    id          UUID                           NOT NULL,
    createdAt   TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    createdBy   VARCHAR(255)                   NOT NULL,
    modifiedAt  TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modifiedBy  VARCHAR(255)                   NOT NULL,
    name        VARCHAR(255),
    description VARCHAR(255),
    user_id     UUID,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

-- changeset floriankolb:1787219813471-6
ALTER TABLE categories ADD CONSTRAINT FK_CATEGORIES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
