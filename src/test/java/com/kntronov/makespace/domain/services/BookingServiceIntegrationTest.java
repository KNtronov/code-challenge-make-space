package com.kntronov.makespace.domain.services;

import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.entities.Room;
import com.kntronov.makespace.domain.entities.TimeSlot;
import com.kntronov.makespace.domain.errors.NoRoomAvailableError;
import com.kntronov.makespace.domain.services.impl.BookingServiceImpl;
import com.kntronov.makespace.infrastructure.repositories.BookingRepositoryImpl;
import com.kntronov.makespace.infrastructure.repositories.SystemStateRepositoryImpl;
import com.kntronov.makespace.testing.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.kntronov.makespace.testing.ResultTesting.expectFailure;
import static com.kntronov.makespace.testing.ResultTesting.expectSuccess;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("BookingService Integration Test")
@Tag("integration")
class BookingServiceIntegrationTest extends IntegrationTest {

    private static final LocalDate date = LocalDate.of(2020, 12, 10);
    private static final Room room1 = new Room("C-Cave", 3);
    private static final Room room2 = new Room("D-Tower", 7);
    private static final Room room3 = new Room("G-Mansion", 20);

    private static final List<Booking> bookings = List.of(
            new Booking(
                    date,
                    new TimeSlot(
                            LocalTime.of(10, 0, 0),
                            LocalTime.of(11, 0, 0)
                    ),
                    room1,
                    3
            ),
            new Booking(
                    date,
                    new TimeSlot(
                            LocalTime.of(10, 15, 0),
                            LocalTime.of(10, 30, 0)
                    ),
                    room3,
                    18
            )
    );

    @Nested
    @DisplayName("getAvailableRooms")
    class GetAvailableRoomsTest {

        @Test
        @DisplayName("""
                when available rooms are retrieved for a date and valid time slot
                should return rooms that were not booked
                """)
        void getAvailableRoomsTest() {
            final var systemRepository = new SystemStateRepositoryImpl(getDataSource());
            final var bookingRepository = new BookingRepositoryImpl(getDataSource());
            bookings.forEach(bookingRepository::save);
            final var subject = new BookingServiceImpl(systemRepository, bookingRepository);

            final var expected = List.of(
                    room2
            );

            final var targetTimeSlot = new TimeSlot(
                    LocalTime.of(9, 45),
                    LocalTime.of(10, 30)
            );
            final var result = subject.getAvailableRooms(date, targetTimeSlot);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("""
                when available rooms are retrieved for a date and the given timeslot overlaps with buffer time
                should return empty list
                """)
        void getAvailableRoomsBufferTimeOverlapTest() {
            final var systemRepository = new SystemStateRepositoryImpl(getDataSource());
            final var bookingRepository = new BookingRepositoryImpl(getDataSource());
            bookings.forEach(bookingRepository::save);
            final var targetTimeSlot = new TimeSlot(
                    LocalTime.of(9, 0),
                    LocalTime.of(9, 15)
            );
            final var subject = new BookingServiceImpl(systemRepository, bookingRepository);

            final var result = subject.getAvailableRooms(date, targetTimeSlot);
            assertThat(result.isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("bookNextAvailableRoom")
    class BookNextAvailableRoomTest {

        @Test
        @DisplayName("when booking slot is found, should create a new booking for most optimal slot and return success")
        void bookNextAvailableRoomSuccessTest() {
            final var systemRepository = new SystemStateRepositoryImpl(getDataSource());
            final var bookingRepository = new BookingRepositoryImpl(getDataSource());
            bookings.forEach(bookingRepository::save);

            final var subject = new BookingServiceImpl(systemRepository, bookingRepository);

            final var targetTimeSlot = new TimeSlot(
                    LocalTime.of(9, 45),
                    LocalTime.of(10, 15)
            );
            final var expected = new Booking(
                    date,
                    targetTimeSlot,
                    room2,
                    2
            );
            final var result = subject.bookNextAvailableRoom(date, targetTimeSlot, 2);
            expectSuccess(result, r ->
                    {
                        assertThat(r).isEqualTo(expected);
                    }
            );
        }

        @Test
        @DisplayName("when booking slot is not found due to insufficient capacity should return failure with NoRoomAvailableError")
        void bookNextAvailableRoomCapacityFailureTest() {
            final var systemRepository = new SystemStateRepositoryImpl(getDataSource());
            final var bookingRepository = new BookingRepositoryImpl(getDataSource());
            bookings.forEach(bookingRepository::save);

            final var subject = new BookingServiceImpl(systemRepository, bookingRepository);

            final var targetTimeSlot = new TimeSlot(
                    LocalTime.of(9, 45),
                    LocalTime.of(10, 15)
            );
            final var result = subject.bookNextAvailableRoom(date, targetTimeSlot, 21);
            expectFailure(result, r ->
                    assertThat(r).hasSameClassAs(new NoRoomAvailableError())
            );
        }

        @Test
        @DisplayName("when booking slot is not found due to all rooms being booked should return failure with NoRoomAvailableError")
        void bookNextAvailableRoomBookedFailureTest() {
            final var systemRepository = new SystemStateRepositoryImpl(getDataSource());
            final var bookingRepository = new BookingRepositoryImpl(getDataSource());
            final var bookings = List.of(
                    new Booking(
                            date,
                            new TimeSlot(
                                    LocalTime.of(10, 0, 0),
                                    LocalTime.of(11, 0, 0)
                            ),
                            room1,
                            3
                    ),
                    new Booking(
                            date,
                            new TimeSlot(
                                    LocalTime.of(10, 0, 0),
                                    LocalTime.of(11, 0, 0)
                            ),
                            room2,
                            3
                    ),
                    new Booking(
                            date,
                            new TimeSlot(
                                    LocalTime.of(10, 0, 0),
                                    LocalTime.of(11, 0, 0)
                            ),
                            room3,
                            3
                    )
            );
            bookings.forEach(bookingRepository::save);

            final var subject = new BookingServiceImpl(systemRepository, bookingRepository);

            final var targetTimeSlot = new TimeSlot(
                    LocalTime.of(9, 45),
                    LocalTime.of(10, 15)
            );
            final var result = subject.bookNextAvailableRoom(date, targetTimeSlot, 15);
            expectFailure(result, r ->
                    assertThat(r).hasSameClassAs(new NoRoomAvailableError())
            );
        }
    }
}
