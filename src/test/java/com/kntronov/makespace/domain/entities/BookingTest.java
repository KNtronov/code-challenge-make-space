package com.kntronov.makespace.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("Booking Test")
class BookingTest {

    private static final LocalDate validDate = LocalDate.of(2020, 12, 10);
    private static final Room validRoom = new Room("valid room", 10);
    private static final TimeSlot validTimeSlot = new TimeSlot(
            LocalTime.of(15, 15, 0, 0),
            LocalTime.of(15, 30, 0, 0)
    );
    private static final int validNumPeople = 10;

    private static final Throwable nullDateException = new IllegalArgumentException("date must not be null");
    private static final Throwable nullRoomException = new IllegalArgumentException("room must not be null");
    private static final Throwable nullTimeSlotException = new IllegalArgumentException("timeSlot must not be null");
    private static final Throwable invalidNumPeopleException = new IllegalArgumentException("numPeople must not be lesser or equal to zero");
    private static final Throwable invalidCapacityException = new IllegalArgumentException("numPeople exceeds room capacity");

    @Test
    @DisplayName("when date is null should fail validation")
    void nullDateTest() {
        assertThatThrownBy(() -> {
            new Booking(null, validTimeSlot, validRoom, validNumPeople);
        }).hasSameClassAs(nullDateException).hasMessage(nullDateException.getMessage());
    }

    @Test
    @DisplayName("when room is null should fail validation")
    void nullRoomTest() {
        assertThatThrownBy(() -> {
            new Booking(validDate, validTimeSlot, null, validNumPeople);
        }).hasSameClassAs(nullRoomException).hasMessage(nullRoomException.getMessage());
    }

    @Test
    @DisplayName("when timeSlot is null should fail validation")
    void nullTimeSlotTest() {
        assertThatThrownBy(() -> {
            new Booking(validDate, null, validRoom, validNumPeople);
        }).hasSameClassAs(nullTimeSlotException).hasMessage(nullTimeSlotException.getMessage());
    }

    @Test
    @DisplayName("when numPeople is zero should fail validation")
    void zeroNumPeopleTest() {
        assertThatThrownBy(() -> {
            new Booking(validDate, validTimeSlot, validRoom, 0);
        }).hasSameClassAs(invalidNumPeopleException).hasMessage(invalidNumPeopleException.getMessage());
    }

    @Test
    @DisplayName("when numPeople is zero should fail validation")
    void negativeNumPeopleTest() {
        assertThatThrownBy(() -> {
            new Booking(validDate, validTimeSlot, validRoom, -1);
        }).hasSameClassAs(invalidNumPeopleException).hasMessage(invalidNumPeopleException.getMessage());
    }

    @Test
    @DisplayName("when number of booking people exceeds capacity should fail validation")
    void invalidCapacityTest() {
        assertThatThrownBy(() -> {
            new Booking(validDate, validTimeSlot, validRoom, 11);
        }).hasSameClassAs(invalidCapacityException).hasMessage(invalidCapacityException.getMessage());
    }
}
