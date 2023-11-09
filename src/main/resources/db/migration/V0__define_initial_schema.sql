create table room
(
    name            VARCHAR,
    people_capacity INTEGER NOT NULL,
    PRIMARY KEY (name)
);

create table booking
(
    id         UUID,
    date       DATE,
    start      TIME,
    "end"      TIME,
    room_name  VARCHAR NOT NULL,
    num_people INTEGER NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (date, start, "end", room_name),
    FOREIGN KEY (room_name) REFERENCES room (name)
);

create table buffer_time
(
    start TIME,
    "end" TIME,
    PRIMARY KEY (start, "end")
)