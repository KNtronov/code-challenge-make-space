package com.kntronov.makespace.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("Booking Test")
class BookingTest {

    private static final Room validRoom = new Room("valid room", 10);
    private static final TimeSlot validTimeSlot = new TimeSlot(
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
}
