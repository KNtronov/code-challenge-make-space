# Make Space Code Challenge

This is a sample project based on this code challenge

https://www.geektrust.com/coding/detailed/make-space

I wrote this as a personal challenge in a few days to revise my Java skills and experiment with a
couple libraries I am (was) not familiar with.

## Project requirements

Make Space Ltd. is a startup offering a co-working space to individuals, freelancers and startups. They provide a common
workspace where anyone can come and work. Along with it, they have dedicated meeting rooms that their customers can book
for private discussions.

Rules

1. Bookings can be made only in a single day from 00:00 to night 23:45. It cannot overlap across days. So you cannot
   book from 23:00 to 01:00, but can from 23:00 to 23:45.
2. A booking can be started and ended only in 15 minute intervals, i.e. XX:00, XX:15, XX:30, XX:45. This means a booking
   can be made at 01:15 or 16:00 but not 15:35 or 16:03
3. The rooms will be allocated only to those who book them, on a first come first serve basis.
4. The most optimal room which can accommodate the number of people will be allocated. For eg., if you asked for a 4
   person capacity requirement then the D-Tower (7 person capacity) will be allocated, provided it is available.
5. In case if the room of desired capacity is not available, the next available capacity room will be allocated. For
   eg., If you asked for the 4 person capacity room between 12:00 to 13:00 and the D-Tower is not available then the
   G-Mansion will be allocated, provided it is available.
6. No meetings can be scheduled during the buffer time. If the booking time overlaps with the buffer time NO_VACANT_ROOM
   should be printed.
7. Time input should follow HH:MM format (24 hours format).

## The cool parts

- This project was designed as a light-weight microservice exposing http api and Postgresql connection.
  I favored simple "magic-less" libraries instead of using the ever-popular SpringBoot or other opinionated frameworks:
    - BuildTool: Maven
    - Web: Javalin
    - Logging: slf4j
    - DB: Postgresql
    - DB access: JDBC + Hikari (connection pool)
    - DB migrations: Flyway
    - Testing: Junit 5 + testcontainers + assertJ
- I decided against using DI (Dependency Injection) frameworks in favor of manual wiring. For such a simple service with
  few dependencies
  using runtime DI or compile time DI is an unnecessary overkill.
- As a personal challenge I avoided using mocking libraries in tests, preferring instead an old-school way of having
  interfaces for services and mock/dummy implementations.
- The architecture is inspired by DDD (Domain Driven Design), in particular
  this [guide](https://learn.microsoft.com/en-us/dotnet/architecture/microservices/microservice-ddd-cqrs-patterns/ddd-oriented-microservice)
  by Microsoft
- I'm using Java 21, at the time of writing the latest LTS Java version. I enjoyed using some of the freshest features
  in my codebase that make the "new" java that much more pleasant to work with.
  Virtual threads are also used indirectly by some of my dependencies, in particular Javalin.

## Known Issues and TODOs

- Hook up a CI pipeline with linters and test coverage checks.
- Integration tests for http API, it will require a re-design of the Javalin server and AppContext into a standalone
  testable unit.
- User management with RBAC or PBAC, ideally integrating with auth0.
- Booking re-scheduling, will probably benefit to redesign for CQRS.
- Document the api with OpenAPI spec.
- Issues with `GET api/rooms/available` sometimes hanging the service (db connection issue?).

## Running this project

This project includes a handy docker-compose.yml to spin up the DB container.
The configuration is performed by ENV variables, a sample for a local execution configuration is provided in .env.local.
The only manual task is to make sure to create the database `makespace` in the db.
Make sure to have your JAVA_HOME env variable set to point to a valid JDK 21.x home.

step-by-step instructions:

1. spin up the DB container with `docker compose up`.
2. if the `makespace` database doesn't exist in the container, run the following SQL script in the
   db `create database makespace`.
3. load ENV variables for a local run with `source .env.local`.
4. make sure `JAVA_HOME` ENV variable is pointing to a valid JDK 21.x home.
5. start up the server with `mvn exec:java`.
6. use your http client of choice to call the endpoints.

## Endpoints

This is a temporary doc to be substituted with a proper OpenAPI spec in time.

### Bookings

#### Create booking for a best matching room

Creates a booking for a time slot and a certain number of people. The booking is created
only if there are available rooms with capacity >= number of people requested (`numPeople`).
If multiple rooms are available, the one with the smallest matching capacity is booked.
If the timeslot requested intersects with the "buffer time", booking cannot be made.

You can check the available rooms and buffer times in the DB or by checking the migrations.

Request

`POST api/bookings/create-best-matching`

sample payload:

```json
{
  "date": "2020-12-10",
  "timeSlotStart": "10:00",
  "timeSlotEnd": "11:30",
  "numPeople": 5
}
```

Response

- 201 CREATED if creation is successful
- 204 NO CONTENT if no available rooms for the time slot and the amount of people requested are present
- 400 BAD REQUEST if request is malformed

#### Get list of bookings for a date

Retrieves all bookings for the requested date.

Request

`GET api/bookings?date=2020-12-10`

Response

- 200 OK and booking list

sample response:

```json
{
  "bookings": [
    {
      "id": "aa2dd5a2-3d14-42e0-b8bd-57b93f6c105a",
      "date": [
        2020,
        12,
        10
      ],
      "timeSlot": {
        "start": [
          10,
          0
        ],
        "end": [
          11,
          30
        ]
      },
      "room": {
        "name": "G-Mansion",
        "peopleCapacity": 20
      },
      "numPeople": 5
    }
  ]
}
```

#### Get booking by id

Retrieves booking by id.

Request

`GET api/bookings/4c040e0c-c890-4139-a405-c8fa6654488e`

Response

- 200 OK and booking if booking for id exists
- 204 NO CONTENT if booking for id does not exist

sample response:

```json
{
  "id": "4c040e0c-c890-4139-a405-c8fa6654488e",
  "date": [
    2020,
    12,
    10
  ],
  "timeSlot": {
    "start": [
      10,
      0
    ],
    "end": [
      11,
      30
    ]
  },
  "room": {
    "name": "D-Tower",
    "peopleCapacity": 7
  },
  "numPeople": 5
}
```

#### Delete booking

Deletes a booking by id.

Request

`DELETE api/bookings/4c040e0c-c890-4139-a405-c8fa6654488e`

Response

- 200 OK and booking if booking for id exists
- 204 NO CONTENT if booking for id does not exist

### Rooms

Retrieves all available (not booked) rooms filtered by date and time slot.

Request

`GET api/rooms/available?date=2020-12-10&from=10:00&to=11:00`

Response

- 200 OK with available rooms

response sample:
```json
{
  "availableRooms": [
    {
      "name": "C-Cave",
      "peopleCapacity": 3
    }
  ]
}
```