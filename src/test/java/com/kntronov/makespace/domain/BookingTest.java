package com.kntronov.makespace.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("Booking Test")
class BookingTest {

    private static final Room validRoom = new Room("valid room", 10);
    private static final Booking.TimeSlot validTimeSlot = new Booking.TimeSlot(
            LocalDate.of(2020, 10, 20),
            LocalTime.of(15, 15, 0, 0),
            LocalTime.of(15, 30, 0, 0)
    );
    private static final int validNumPeople = 10;

    private static final Throwable nullRoomException = new IllegalArgumentException("room must not be null");
    private static final Throwable nullTimeSlotException = new IllegalArgumentException("timeSlot must not be null");
    private static final Throwable invalidNumPeopleException = new IllegalArgumentException("numPeople must not be lesser or equal to zero");
    private static final Throwable invalidCapacityException = new IllegalArgumentException("numPeople exceeds room capacity");

    @Test
    @DisplayName("when room is null should fail validation")
    void nullRoomTest() {
        assertThatThrownBy(() -> {
            new Booking(null, validTimeSlot, validNumPeople);
        }).hasSameClassAs(nullRoomException).hasMessage(nullRoomException.getMessage());
    }

    @Test
    @DisplayName("when timeSlot is null should fail validation")
    void nullTimeSlotTest() {
        assertThatThrownBy(() -> {
            new Booking(validRoom, null, validNumPeople);
        }).hasSameClassAs(nullTimeSlotException).hasMessage(nullTimeSlotException.getMessage());
    }

    @Test
    @DisplayName("when numPeople is zero should fail validation")
    void zeroNumPeopleTest() {
        assertThatThrownBy(() -> {
            new Booking(validRoom, validTimeSlot, 0);
        }).hasSameClassAs(invalidNumPeopleException).hasMessage(invalidNumPeopleException.getMessage());
    }

    @Test
    @DisplayName("when numPeople is zero should fail validation")
    void negativeNumPeopleTest() {
        assertThatThrownBy(() -> {
            new Booking(validRoom, validTimeSlot, -1);
        }).hasSameClassAs(invalidNumPeopleException).hasMessage(invalidNumPeopleException.getMessage());
    }

    @Test
    @DisplayName("when number of booking people exceeds capacity should fail validation")
    void invalidCapacityTest() {
        assertThatThrownBy(() -> {
            new Booking(validRoom, validTimeSlot, 11);
        }).hasSameClassAs(invalidCapacityException).hasMessage(invalidCapacityException.getMessage());
    }

    @Nested
    @DisplayName("TimeSlot Test")
    class TimeSlotTest {

        private static final LocalDate validDate = LocalDate.of(2020, 12, 10);
        private static final LocalTime validStart = LocalTime.of(14, 30, 0);
        private static final LocalTime validEnd = LocalTime.of(14, 45, 0);
        private static final Throwable nullDateException = new IllegalArgumentException("date must not be null");
        private static final Throwable nullStartException = new IllegalArgumentException("start must not be null");
        private static final Throwable nullEndException = new IllegalArgumentException("end must not be null");
        private static final Throwable invalidTimeSlotException = new IllegalArgumentException("slot minutes must be one of [0, 15, 30, 45]");
        private static final Throwable invalidRangeException = new IllegalArgumentException("end must be after start");

        private static Stream<Arguments> provideInvalidTimeSlotValues() {
            return Stream.of(
                    Arguments.of(1),
                    Arguments.of(14),
                    Arguments.of(16),
                    Arguments.of(29),
                    Arguments.of(31),
                    Arguments.of(44),
                    Arguments.of(46),
                    Arguments.of(59)
            );
        }

        @Test
        @DisplayName("when date is null should fail validation")
        void nullDateTest() {
            assertThatThrownBy(() -> {
                new Booking.TimeSlot(null, validStart, validEnd);
            }).hasSameClassAs(nullDateException).hasMessage(nullDateException.getMessage());
        }

        @Test
        @DisplayName("when start is null should fail validation")
        void nullStartTest() {
            assertThatThrownBy(() -> {
                new Booking.TimeSlot(validDate, null, validEnd);
            }).hasSameClassAs(nullStartException).hasMessage(nullStartException.getMessage());
        }

        @Test
        @DisplayName("when end is null should fail validation")
        void nullEndTest() {
            assertThatThrownBy(() -> {
                new Booking.TimeSlot(validDate, validStart, null);
            }).hasSameClassAs(nullEndException).hasMessage(nullEndException.getMessage());
        }

        @ParameterizedTest
        @MethodSource("provideInvalidTimeSlotValues")
        @DisplayName("when start has invalid format should fail validation")
        void invalidStartTimeTest(int invalidMinutes) {
            final var invalidStart = LocalTime.of(12, invalidMinutes, 0);
            assertThatThrownBy(() -> {
                new Booking.TimeSlot(validDate, invalidStart, validEnd);
            }).hasSameClassAs(invalidTimeSlotException).hasMessage(invalidTimeSlotException.getMessage());
        }

        @ParameterizedTest
        @MethodSource("provideInvalidTimeSlotValues")
        @DisplayName("when end has invalid format should fail validation")
        void invalidEndTimeTest(int invalidMinutes) {
            final var invalidEnd = LocalTime.of(12, invalidMinutes, 0);
            assertThatThrownBy(() -> {
                new Booking.TimeSlot(validDate, validStart, invalidEnd);
            }).hasSameClassAs(invalidTimeSlotException).hasMessage(invalidTimeSlotException.getMessage());
        }

        @Test
        @DisplayName("when start and end are an invalid range should fail validation")
        void invalidRangeTest() {
            assertThatThrownBy(() -> {
                new Booking.TimeSlot(validDate, validEnd, validStart);
            }).hasSameClassAs(invalidRangeException).hasMessage(invalidRangeException.getMessage());
        }
    }
}
