-- liquibase formatted sql

-- changeset floriankolb:1787500000000-1
CREATE TABLE items
(
    id               UUID                           NOT NULL,
    createdAt        TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    createdBy        VARCHAR(255)                   NOT NULL,
    modifiedAt       TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modifiedBy       VARCHAR(255)                   NOT NULL,
    name             VARCHAR(255),
    description      VARCHAR(1000),
    serialNumber     VARCHAR(255),
    modelNumber      VARCHAR(255),
    manufacturer     VARCHAR(255),
    quantity         INTEGER                        NOT NULL DEFAULT 1,
    sold             BOOLEAN                        NOT NULL DEFAULT FALSE,
    category_id      UUID,
    storage_id       UUID,
    user_id          UUID,
    CONSTRAINT pk_items PRIMARY KEY (id)
);

-- changeset floriankolb:1787500000000-2
ALTER TABLE items ADD CONSTRAINT FK_ITEMS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset floriankolb:1787500000000-3
ALTER TABLE items ADD CONSTRAINT FK_ITEMS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

-- changeset floriankolb:1787500000000-4
ALTER TABLE items ADD CONSTRAINT FK_ITEMS_ON_STORAGE FOREIGN KEY (storage_id) REFERENCES storages (id);
