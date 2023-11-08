create table room
(
    name            VARCHAR,
    people_capacity INTEGER NOT NULL,
    PRIMARY KEY (name)
);

create table booking
(
    date       DATE,
    start      TIME,
    "end"      TIME,
    room_name  VARCHAR NOT NULL,
    num_people INTEGER NOT NULL,
    PRIMARY KEY (date, start, "end"),
    FOREIGN KEY (room_name) REFERENCES room (name)
);