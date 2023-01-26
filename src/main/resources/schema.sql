DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS users CASCADE;


CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255)                            NOT NULL,
    description VARCHAR(2000)                           NOT NULL,
    available   BOOLEAN,
    owner_id    BIGINT                                  NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT FK_ITEM_USER FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP,
    end_date   TIMESTAMP,
    item_id    BIGINT,
    booker_id  BIGINT,
    status     VARCHAR(25),
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT FK_BOOKING_ITEM FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT FK_BOOKING_USER FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      VARCHAR(5000),
    item_id   BIGINT,
    author_id BIGINT,
    created   TIMESTAMP,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT FK_COMMENT_ITEM FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT FK_COMMENT_USER FOREIGN KEY (author_id) REFERENCES users (id)
);