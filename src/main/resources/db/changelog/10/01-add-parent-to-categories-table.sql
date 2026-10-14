-- liquibase formatted sql

-- changeset floriankolb:1787400000000-1
ALTER TABLE categories ADD COLUMN parent_id UUID;

-- changeset floriankolb:1787400000000-2
ALTER TABLE categories ADD CONSTRAINT FK_CATEGORIES_ON_PARENT FOREIGN KEY (parent_id) REFERENCES categories (id);
