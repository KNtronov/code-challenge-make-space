package com.kntronov.makespace.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("Room Test")
class RoomTest {

    private static final String validRoomName = "valid room";
    private static final int validRoomCapacity = 10;

    private static final Throwable nullNameException = new IllegalArgumentException("name must not be null");
    private static final Throwable blankNameException = new IllegalArgumentException("name must not be blank");
    private static final Throwable invalidCapacityException = new IllegalArgumentException("peopleCapacity must not be lesser or equal to zero");

    @Test
    @DisplayName("when name is null should fail validation")
    void nullNameTest() {
        assertThatThrownBy(() -> {
            new Room(null, validRoomCapacity);
        }).hasSameClassAs(nullNameException).hasMessage(nullNameException.getMessage());
    }

    @Test
    @DisplayName("when name is blank should fail validation")
    void blankNameTest() {
        assertThatThrownBy(() -> {
            new Room("", validRoomCapacity);
        }).hasSameClassAs(blankNameException).hasMessage(blankNameException.getMessage());
    }

    @Test
    @DisplayName("when capacity is zero should fail validation")
    void zeroCapacityTest() {
        assertThatThrownBy(() -> {
            new Room(validRoomName, 0);
        }).hasSameClassAs(invalidCapacityException).hasMessage(invalidCapacityException.getMessage());
    }

    @Test
    @DisplayName("when capacity is negative should fail validation")
    void negativeCapacityTest() {
        assertThatThrownBy(() -> {
            new Room(validRoomName, -1);
        }).hasSameClassAs(invalidCapacityException).hasMessage(invalidCapacityException.getMessage());
    }
}
