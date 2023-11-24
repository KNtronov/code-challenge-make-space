package com.kntronov.makespace.application.routes;

import com.kntronov.makespace.application.AppErrors;
import com.kntronov.makespace.application.JavalinTestApp;
import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.entities.Room;
import com.kntronov.makespace.domain.entities.TimeSlot;
import com.kntronov.makespace.domain.errors.NoRoomAvailableException;
import com.kntronov.makespace.domain.errors.RoomNotFoundException;
import com.kntronov.makespace.domain.services.BookingService;
import com.kntronov.makespace.testing.Mocks;
import com.kntronov.makespace.testing.TestTags;
import com.kntronov.makespace.util.Nothing;
import com.kntronov.makespace.util.Result;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("BookingRoutes Integration Test")
@Tag(TestTags.INTEGRATION_TEST)
class BookingRoutesTest {

    private static final UUID bookingId1 = UUID.fromString("e58ed763-928c-4155-bee9-fdbaaadc1111");
    private static final UUID bookingId2 = UUID.fromString("e58ed763-928c-4155-bee9-fdbaaadc2222");

    private static final UUID bookingId3 = UUID.fromString("e58ed763-928c-4155-bee9-fdbaaadc3333");
    private static final String stringExpectedDate = "2020-12-12";
    private static final LocalDate expectedDate = LocalDate.of(2020, 12, 10);
    private static final Room room1 = new Room("C-Cave", 3);
    private static final Booking booking1 = new Booking(
            bookingId1,
            expectedDate,
            new TimeSlot(
                    LocalTime.of(10, 0, 0),
                    LocalTime.of(11, 0, 0)
            ),
            room1,
            3
    );

    @Nested
    @DisplayName("/bookings/create-best-matching")
    class CreateBestMatchingTest {
        private static final String expectedResponseJson = """
                {"id":"e58ed763-928c-4155-bee9-fdbaaadc1111","date":[2020,12,10],"timeSlot":{"start":[10,0],"end":[11,0]},"room":{"name":"C-Cave","peopleCapacity":3},"numPeople":3}""";
        private static final String validDate = "2020-12-12";
        private static final String validTimeSlotStart = "10:00";
        private static final String validTimeSlotEnd = "11:30";
        private static final String validNumPeople = "5";
        private final Javalin subject = new JavalinTestApp() {
            @Override
            protected BookingService bookingService() {
                return new Mocks.BookingServiceMock() {
                    /*
                    numPeople = 1 -> success
                    numPeople = 2 -> success not found
                    numPeople = 3 -> fail
                    */
                    @Override
                    public Result<Booking> bookNextAvailableRoom(LocalDate date, TimeSlot timeSlot, int numPeople) {
                        if (numPeople == 1) {
                            return Result.pure(booking1);
                        } else if (numPeople == 2) {
                            return Result.fail(new NoRoomAvailableException());
                        } else {
                            return Result.fail(new RuntimeException());
                        }
                    }
                };
            }
        }.subject();

        private static String createRequestJson(
                String date,
                String timeSlotStart,
                String timeSlotEnd,
                String numPeople
        ) {
            final var builder = new StringBuilder("{");
            if (date != null) {
                builder.append("\"date\": \"%s\",".formatted(date));
            }
            if (timeSlotStart != null) {
                builder.append("\"timeSlotStart\": \"%s\",".formatted(timeSlotStart));
            }
            if (timeSlotEnd != null) {
                builder.append("\"timeSlotEnd\": \"%s\"".formatted(timeSlotEnd));
                if (numPeople != null) {
                    builder.append(",");
                }
            }
            if (numPeople != null) {
                builder.append("\"numPeople\": %s".formatted(numPeople));
            }
            builder.append("}");
            return builder.toString();
        }

        private static Stream<Arguments> provideInvalidRequestCases() {
            return Stream.of(
                    Arguments.of(createRequestJson(null, validTimeSlotStart, validTimeSlotEnd, validNumPeople), "date is mandatory"),
                    Arguments.of(createRequestJson(validDate, null, validTimeSlotEnd, validNumPeople), "timeSlotStart is mandatory"),
                    Arguments.of(createRequestJson(validDate, validTimeSlotStart, null, validNumPeople), "timeSlotEnd is mandatory"),
                    Arguments.of(createRequestJson(validDate, validTimeSlotStart, validTimeSlotEnd, "0"), "numPeople must be > 0"),
                    Arguments.of(createRequestJson(validDate, validTimeSlotStart, validTimeSlotEnd, "-1"), "numPeople must be > 0"),
                    Arguments.of(createRequestJson(validDate, validTimeSlotEnd, validTimeSlotStart, validNumPeople), "timeSlotEnd must be after timeSlotStart"),
                    Arguments.of(createRequestJson(validDate, "10:13", validTimeSlotEnd, validNumPeople), "timeSlotStart minutes must be in [0, 15, 30, 45]"),
                    Arguments.of(createRequestJson(validDate, validTimeSlotStart, "11:13", validNumPeople), "timeSlotEnd minutes must be in [0, 15, 30, 45]")
            );
        }

        @Test
        @DisplayName("when POST is called and creation is successful should return 201 and created booking")
        void testCreate() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.post("/api/bookings/create-best-matching", createRequestJson(
                        validDate,
                        validTimeSlotStart,
                        validTimeSlotEnd,
                        "1"
                ));
                assertThat(result.code()).isEqualTo(201);
                assertThat(result.body().string()).isEqualTo(expectedResponseJson);
            });
        }

        @Test
        @DisplayName("when POST is called and no available room is found should return 204")
        void testCreateNoMatch() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.post("/api/bookings/create-best-matching", createRequestJson(
                        validDate,
                        validTimeSlotStart,
                        validTimeSlotEnd,
                        "2"
                ));
                assertThat(result.code()).isEqualTo(204);
            });
        }

        @Test
        @DisplayName("when POST is called and booking creation fails found should return 500")
        void testCreateError() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.post("/api/bookings/create-best-matching", createRequestJson(
                        validDate,
                        validTimeSlotStart,
                        validTimeSlotEnd,
                        "3"
                ));
                assertThat(result.code()).isEqualTo(500);
            });
        }

        @ParameterizedTest
        @MethodSource("provideInvalidRequestCases")
        @DisplayName("when POST is called with innvalid body should return 400 and error")
        void testCreateInvalidRequest(String requestJson, String expectedError) {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.post("/api/bookings/create-best-matching", requestJson);
                assertThat(result.code()).isEqualTo(400);
                assertThat(result.body().string()).isEqualTo(AppErrors.badRequestError("[REQUEST_BODY] error: " + expectedError));
            });
        }
    }

    @Nested
    @DisplayName("/bookings")
    class BookingsRootTest {

        private static final String expectedResponseJson = """
                {"bookings":[{"id":"e58ed763-928c-4155-bee9-fdbaaadc1111","date":[2020,12,10],"timeSlot":{"start":[10,0],"end":[11,0]},"room":{"name":"C-Cave","peopleCapacity":3},"numPeople":3}]}""";

        private final Javalin subject = new JavalinTestApp() {
            @Override
            protected BookingService bookingService() {
                return new Mocks.BookingServiceMock() {
                    @Override
                    public List<Booking> getAllBookingsByDate(LocalDate date) {
                        return List.of(
                                booking1
                        );
                    }
                };
            }
        }.subject();

        @Test
        @DisplayName("when GET is called with valid date query param should return 200 and all bookings for date")
        void testGetAll() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.get("/api/bookings?date=" + stringExpectedDate);
                assertThat(result.code()).isEqualTo(200);
                assertThat(result.body().string()).isEqualTo(expectedResponseJson);
            });
        }

        @Test
        @DisplayName("when GET is called with malformed date query param should return 400 and error")
        void testGetAllInvalidDate() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.get("/api/bookings?date=xxxx");
                assertThat(result.code()).isEqualTo(400);
                assertThat(result.body().string()).isEqualTo(AppErrors.badRequestError("[date] error: TYPE_CONVERSION_FAILED"));
            });
        }

        @Test
        @DisplayName("when GET is called without valid date query param should return 400 and error")
        void testGetAllMissingDate() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.get("/api/bookings");
                assertThat(result.code()).isEqualTo(400);
                assertThat(result.body().string()).isEqualTo(AppErrors.badRequestError("[date] error: NULLCHECK_FAILED"));
            });
        }

    }

    @Nested
    @DisplayName("/bookings/{id}")
    class BookingsWithIdTest {

        private static final String expectedResponseJson = """
                {"id":"e58ed763-928c-4155-bee9-fdbaaadc1111","date":[2020,12,10],"timeSlot":{"start":[10,0],"end":[11,0]},"room":{"name":"C-Cave","peopleCapacity":3},"numPeople":3}""";

        private final Javalin subject = new JavalinTestApp() {
            @Override
            protected BookingService bookingService() {
                return new Mocks.BookingServiceMock() {
                    /*
                    id1 -> success
                    id2 -> success not found
                    id3 -> fail
                    */
                    @Override
                    public Result<Booking> getBooking(UUID id) {
                        if (id.equals(bookingId1)) {
                            return Result.pure(booking1);
                        } else if (id.equals(bookingId2)) {
                            return Result.fail(new RoomNotFoundException());
                        } else {
                            return Result.fail(new RuntimeException());
                        }
                    }

                    @Override
                    public Result<Nothing> deleteBooking(UUID id) {
                        if (id.equals(bookingId1)) {
                            return Result.pure(Nothing.get());
                        } else if (id.equals(bookingId2)) {
                            return Result.fail(new RoomNotFoundException());
                        } else {
                            return Result.fail(new RuntimeException());
                        }
                    }
                };
            }
        }.subject();

        @Test
        @DisplayName("when GET is called and booking is present should return 200 and booking")
        void getBookingTest() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.get("/api/bookings/" + bookingId1);
                assertThat(result.code()).isEqualTo(200);
                assertThat(result.body().string()).isEqualTo(expectedResponseJson);
            });
        }

        @Test
        @DisplayName("when GET is called and booking does not exist should return 204")
        void getBookingNoBookingTest() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.get("/api/bookings/" + bookingId2);
                assertThat(result.code()).isEqualTo(204);
            });
        }

        @Test
        @DisplayName("when GET is called and booking retrieval fails should return 500")
        void getBookingErrorTest() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.get("/api/bookings/" + bookingId3);
                assertThat(result.code()).isEqualTo(500);
                assertThat(result.body().string()).isEqualTo(AppErrors.internalServerError());
            });
        }

        @Test
        @DisplayName("when GET is called and uuid is malformed should return 400 and error")
        void getBookingTestMalformedId() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.get("/api/bookings/xxxx");
                assertThat(result.code()).isEqualTo(400);
                assertThat(result.body().string()).isEqualTo(AppErrors.badRequestError("[id] error: TYPE_CONVERSION_FAILED"));
            });
        }

        @Test
        @DisplayName("when DELETE is called and booking is present should return 200 and booking")
        void deleteBookingTest() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.delete("/api/bookings/" + bookingId1);
                assertThat(result.code()).isEqualTo(200);
            });
        }

        @Test
        @DisplayName("when DELETE is called and booking does not exist should return 204")
        void deleteBookingNoBookingTest() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.delete("/api/bookings/" + bookingId2);
                assertThat(result.code()).isEqualTo(204);
            });
        }

        @Test
        @DisplayName("when DELETE is called and booking retrieval fails should return 500")
        void deleteBookingErrorTest() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.delete("/api/bookings/" + bookingId3);
                assertThat(result.code()).isEqualTo(500);
                assertThat(result.body().string()).isEqualTo(AppErrors.internalServerError());
            });
        }

        @Test
        @DisplayName("when DELETE is called and uuid is malformed should return 400 and error")
        void deleteBookingTestMalformedId() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.delete("/api/bookings/xxxx");
                assertThat(result.code()).isEqualTo(400);
                assertThat(result.body().string()).isEqualTo(AppErrors.badRequestError("[id] error: TYPE_CONVERSION_FAILED"));
            });
        }
    }
}
