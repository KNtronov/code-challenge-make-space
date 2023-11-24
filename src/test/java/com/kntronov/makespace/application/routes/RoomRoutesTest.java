package com.kntronov.makespace.application.routes;

import com.kntronov.makespace.application.JavalinTestApp;
import com.kntronov.makespace.domain.entities.Room;
import com.kntronov.makespace.domain.entities.TimeSlot;
import com.kntronov.makespace.domain.services.BookingService;
import com.kntronov.makespace.testing.Mocks;
import com.kntronov.makespace.testing.TestTags;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("RoomRoutes Integration Test")
@Tag(TestTags.INTEGRATION_TEST)
class RoomRoutesTest {

    private static final Room room = new Room("C-Cave", 3);

    @Nested
    @DisplayName("/rooms/available")
    class AvailableRoomsTest {

        private static final String date = "2020-12-12";
        private static final String from = "10:00";
        private static final String to = "11:00";
        private static String expectedResponseJson = """
                {"availableRooms":[{"name":"C-Cave","peopleCapacity":3}]}""";
        private final Javalin subject = new JavalinTestApp() {
            @Override
            protected BookingService bookingService() {
                return new Mocks.BookingServiceMock() {
                    @Override
                    public List<Room> getAvailableRooms(LocalDate date, TimeSlot timeSlot) {
                        return List.of(room);
                    }
                };
            }
        }.subject();

        @Test
        @DisplayName("when GET is called should return 200 and rooms")
        void getRoomsTest() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.get("/api/rooms/available?date=" + date + "&from=" + from + "&to=" + to);
                assertThat(result.code()).isEqualTo(200);
                assertThat(result.body().string()).isEqualTo(expectedResponseJson);
            });
        }

        @Test
        @DisplayName("when GET is called and date is missing should return 400 and error")
        void getRoomsNoDateTest() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.get("/api/rooms/available?from=" + from + "&to=" + to);
                assertThat(result.code()).isEqualTo(400);
            });
        }

        @Test
        @DisplayName("when GET is called and from is missing should return 400 and error")
        void getRoomsNoFromTest() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.get("/api/rooms/available?date" + date + "&to=" + to);
                assertThat(result.code()).isEqualTo(400);
            });
        }

        @Test
        @DisplayName("when GET is called and to is missing should return 400 and error")
        void getRoomsNoToTest() {
            JavalinTest.test(subject, (server, client) -> {
                final var result = client.get("/api/rooms/available?date" + date + "&from=" + from);
                assertThat(result.code()).isEqualTo(400);
            });
        }
    }

}
