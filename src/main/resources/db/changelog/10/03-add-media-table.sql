-- liquibase formatted sql

-- changeset floriankolb:1787600000000-1
CREATE TABLE media
(
    id          UUID                           NOT NULL,
    createdAt   TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    createdBy   VARCHAR(255)                   NOT NULL,
    modifiedAt  TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    modifiedBy  VARCHAR(255)                   NOT NULL,
    fileName    VARCHAR(255),
    contentType VARCHAR(255)                   NOT NULL,
    fileSize    BIGINT                         NOT NULL,
    data        BYTEA                          NOT NULL,
    item_id     UUID,
    user_id     UUID,
    CONSTRAINT pk_media PRIMARY KEY (id)
);

-- changeset floriankolb:1787600000000-2
ALTER TABLE media ADD CONSTRAINT FK_MEDIA_ON_ITEM FOREIGN KEY (item_id) REFERENCES items (id);

-- changeset floriankolb:1787600000000-3
ALTER TABLE media ADD CONSTRAINT FK_MEDIA_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
