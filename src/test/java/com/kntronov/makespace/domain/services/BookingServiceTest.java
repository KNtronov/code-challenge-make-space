package com.kntronov.makespace.domain.services;

import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.entities.Room;
import com.kntronov.makespace.domain.entities.SystemState;
import com.kntronov.makespace.domain.entities.TimeSlot;
import com.kntronov.makespace.domain.errors.NoRoomAvailableException;
import com.kntronov.makespace.domain.services.impl.BookingServiceImpl;
import com.kntronov.makespace.testing.Captor;
import com.kntronov.makespace.testing.Mocks;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.kntronov.makespace.testing.ResultTesting.expectFailure;
import static com.kntronov.makespace.testing.ResultTesting.expectSuccess;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("BookingService Test")
class BookingServiceTest {

          /*
        Setting up this situation

        (X = booked, O = buffer time)
        -------------------------------------------------------------------------------------------
        name, capacity / time | 9.00 | 9.15 | 9.30 | 9.45 | 10.00 | 10.15 | 10.30 | 10.45 | 11.00 |
        -------------------------------------------------------------------------------------------
        room1 (10)            |  O   |  O   |  O   |   O  |   X   |   X   |   X   |   X   |   X   |
        -------------------------------------------------------------------------------------------
        room2 (5)             |  O   |  O   |  O   |   O  |       |       |       |       |       |
        -------------------------------------------------------------------------------------------
        room3 (2)             |  O   |  O   |  O   |   O  |       |   X   |   X   |       |       |
        -------------------------------------------------------------------------------------------
         */

    private static final UUID bookingId1 = UUID.fromString("e58ed763-928c-4155-bee9-fdbaaadc1111");
    private static final UUID bookingId2 = UUID.fromString("e58ed763-928c-4155-bee9-fdbaaadc2222");
    private static final UUID bookingId3 = UUID.fromString("e58ed763-928c-4155-bee9-fdbaaadc3333");
    private static final UUID newBookingId = UUID.fromString("e58ed763-928c-4155-bee9-fdbaaadc0000");
    private static final LocalDate date = LocalDate.of(2020, 12, 10);
    private static final Room room1 = new Room("room1", 10);
    private static final Room room2 = new Room("room2", 5);
    private static final Room room3 = new Room("room3", 2);
    private static final List<Room> rooms = List.of(
            room1, room2, room3
    );
    private static final List<Booking> bookings = List.of(
            new Booking(
                    bookingId1,
                    date,
                    new TimeSlot(
                            LocalTime.of(10, 0, 0),
                            LocalTime.of(11, 0, 0)
                    ),
                    room1,
                    8
            ),
            new Booking(
                    bookingId2,
                    date,
                    new TimeSlot(
                            LocalTime.of(10, 15, 0),
                            LocalTime.of(10, 30, 0)
                    ),
                    room3,
                    2
            )
    );
    private static final List<TimeSlot> bufferTimes = List.of(
            new TimeSlot(
                    LocalTime.of(9, 0, 0),
                    LocalTime.of(9, 45, 0)
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
            final var systemRepositoryMock = new Mocks.SystemStateRepositoryMock() {
                @Override
                public SystemState findByDate(LocalDate date) {
                    return new SystemState(date, rooms, bookings, bufferTimes);
                }
            };
            final var bookingRepositoryMock = new Mocks.BookingRepositoryMock() {
            };
            final var uuidProvider = new Mocks.UUIDProviderMock(List.of(newBookingId));
            final var subject = new BookingServiceImpl(uuidProvider, systemRepositoryMock, bookingRepositoryMock);

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
            final var targetTimeSlot = new TimeSlot(
                    LocalTime.of(9, 30),
                    LocalTime.of(10, 30)
            );
            final var systemRepositoryMock = new Mocks.SystemStateRepositoryMock() {
                @Override
                public SystemState findByDate(LocalDate date) {
                    return new SystemState(date, rooms, bookings, bufferTimes);
                }
            };
            final var bookingRepositoryMock = new Mocks.BookingRepositoryMock() {

            };
            final var uuidProvider = new Mocks.UUIDProviderMock(List.of(newBookingId));
            final var subject = new BookingServiceImpl(uuidProvider, systemRepositoryMock, bookingRepositoryMock);

            final var result = subject.getAvailableRooms(date, targetTimeSlot);
            assertThat(result.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("""
                when available rooms are retrieved for a date and a valid timeslot
                and no rooms are available
                should return empty list
                """)
        void getAvailableRoomsNoAvailabilityTest() {
            final var systemRepositoryMock = new Mocks.SystemStateRepositoryMock() {
                @Override
                public SystemState findByDate(LocalDate date) {
                    return new SystemState(date, List.of(room1), bookings, bufferTimes);
                }
            };
            final var bookingRepositoryMock = new Mocks.BookingRepositoryMock() {
            };
            final var uuidProvider = new Mocks.UUIDProviderMock(List.of(newBookingId));
            final var subject = new BookingServiceImpl(uuidProvider, systemRepositoryMock, bookingRepositoryMock);

            final var targetTimeSlot = new TimeSlot(
                    LocalTime.of(9, 45),
                    LocalTime.of(10, 30)
            );
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
            final var systemRepositoryMock = new Mocks.SystemStateRepositoryMock() {
                @Override
                public SystemState findByDate(LocalDate date) {
                    return new SystemState(date, rooms, bookings, bufferTimes);
                }
            };
            final var savedBookingCaptor = new Captor<Booking>();
            final var bookingRepositoryMock = new Mocks.BookingRepositoryMock() {
                @Override
                public Booking save(Booking booking) {
                    savedBookingCaptor.capture(booking);
                    return booking;
                }
            };

            final var uuidProvider = new Mocks.UUIDProviderMock(List.of(newBookingId));
            final var subject = new BookingServiceImpl(uuidProvider, systemRepositoryMock, bookingRepositoryMock);


            final var targetTimeSlot = new TimeSlot(
                    LocalTime.of(9, 45),
                    LocalTime.of(10, 15)
            );
            final var expected = new Booking(
                    newBookingId,
                    date,
                    targetTimeSlot,
                    room3,
                    2
            );
            final var result = subject.bookNextAvailableRoom(date, targetTimeSlot, 2);
            expectSuccess(result, r ->
                    {
                        assertThat(r).isEqualTo(expected);
                        assertThat(savedBookingCaptor.getLast()).isEqualTo(r);
                    }
            );
        }

        @Test
        @DisplayName("when booking slot is not found due to insufficient capacity should return failure with NoRoomAvailableError")
        void bookNextAvailableRoomCapacityFailureTest() {
            final var systemRepositoryMock = new Mocks.SystemStateRepositoryMock() {
                @Override
                public SystemState findByDate(LocalDate date) {
                    return new SystemState(date, rooms, bookings, bufferTimes);
                }
            };
            final var bookingRepositoryMock = new Mocks.BookingRepositoryMock() {
            };
            final var uuidProvider = new Mocks.UUIDProviderMock(List.of(newBookingId));

            final var subject = new BookingServiceImpl(uuidProvider, systemRepositoryMock, bookingRepositoryMock);

            final var targetTimeSlot = new TimeSlot(
                    LocalTime.of(9, 45),
                    LocalTime.of(10, 15)
            );
            final var result = subject.bookNextAvailableRoom(date, targetTimeSlot, 15);
            expectFailure(result, r ->
                    assertThat(r).hasSameClassAs(new NoRoomAvailableException())
            );
        }

        @Test
        @DisplayName("when booking slot is not found due to all rooms being booked should return failure with NoRoomAvailableError")
        void bookNextAvailableRoomBookedFailureTest() {
            final var systemRepositoryMock = new Mocks.SystemStateRepositoryMock() {
                @Override
                public SystemState findByDate(LocalDate date) {
                    return new SystemState(date, List.of(room1), bookings, bufferTimes);
                }
            };
            final var bookingRepositoryMock = new Mocks.BookingRepositoryMock() {
            };
            final var uuidProvider = new Mocks.UUIDProviderMock(List.of(newBookingId));

            final var subject = new BookingServiceImpl(uuidProvider, systemRepositoryMock, bookingRepositoryMock);

            final var targetTimeSlot = new TimeSlot(
                    LocalTime.of(9, 45),
                    LocalTime.of(10, 15)
            );
            final var result = subject.bookNextAvailableRoom(date, targetTimeSlot, 15);
            expectFailure(result, r ->
                    assertThat(r).hasSameClassAs(new NoRoomAvailableException())
            );
        }
    }
}
