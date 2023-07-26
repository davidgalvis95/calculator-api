SELECT * FROM pg_extension WHERE extname = 'uuid-ossp';
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS operation (
    operation_id uuid NOT NULL,
    cost integer,
    type character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT operation_pkey PRIMARY KEY (operation_id),
    CONSTRAINT operation_type_check CHECK (type::text = ANY (ARRAY['ADDITION'::character varying, 'SUBTRACTION'::character varying, 'MULTIPLICATION'::character varying, 'DIVISION'::character varying, 'SQUARE_ROOT'::character varying, 'RANDOM_STRING'::character varying]::text[])),
    CONSTRAINT operation_type_unique UNIQUE (type)
);

INSERT INTO operation (operation_id, type, cost)
VALUES (uuid_generate_v4(), 'ADDITION', 1),
       (uuid_generate_v4(), 'SUBTRACTION', 1),
       (uuid_generate_v4(), 'MULTIPLICATION', 2),
       (uuid_generate_v4(), 'DIVISION', 2),
       (uuid_generate_v4(), 'SQUARE_ROOT', 3),
       (uuid_generate_v4(), 'RANDOM_STRING', 4)
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS roles
(
    role_id uuid NOT NULL,
    type character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT roles_pkey PRIMARY KEY (role_id),
    CONSTRAINT uk_q9npl2ty4pngm2cussiul2qj5 UNIQUE (type),
    CONSTRAINT roles_type_check CHECK (type::text = ANY (ARRAY['USER'::character varying, 'ADMIN'::character varying]::text[]))
);

INSERT INTO roles (role_id, type)
VALUES (uuid_generate_v4(), 'USER'),
       (uuid_generate_v4(), 'ADMIN') ON CONFLICT DO NOTHING;

INSERT INTO users (user_id, balance, email, password, status)
SELECT uuid_generate_v4(), 100, 'admin@yopmail.com',
       '$2a$10$D8TKYxRy6uOZ6MVw9eLUs.CrvyN52n2kjqJOZ6b7mltDGPJi1rcxC', 'ACTIVE'
    WHERE NOT EXISTS (SELECT 1 FROM users);

WITH
    user_data AS (
        SELECT user_id
        FROM users LIMIT 1
    ),
    role_data_admin AS (
        SELECT role_id
        FROM roles
        WHERE type='ADMIN'
    ),
    role_data_user AS (
        SELECT role_id
        FROM roles
        WHERE type='USER'
    )
INSERT INTO user_roles (user_id, role_id)
    SELECT user_id, role_id
    FROM user_data, role_data_admin WHERE NOT EXISTS (SELECT 1 FROM user_roles)
    UNION ALL
    SELECT user_id, role_id
    FROM user_data, role_data_user WHERE NOT EXISTS (SELECT 1 FROM user_roles);
