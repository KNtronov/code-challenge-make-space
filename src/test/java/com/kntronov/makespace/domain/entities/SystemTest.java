package com.kntronov.makespace.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("System Test")
class SystemTest {

    private static final LocalDate validDate = LocalDate.of(2020, 12, 10);
    private static final Room room1 = new Room("room1", 10);
    private static final List<Room> validRooms = List.of(
            room1
    );
    private static final List<Booking> validBookings = List.of(
            new Booking(
                    room1,
                    new TimeSlot(
                            LocalTime.of(14, 0, 0),
                            LocalTime.of(14, 30, 0)
                    ),
                    8
            )
    );

    private static final TimeSlot validBufferTime = new TimeSlot(
            LocalTime.of(9, 0, 0),
            LocalTime.of(9, 30, 0)
    );

    private static final Throwable nullDateException = new IllegalArgumentException("date must not be null");
    private static final Throwable nullRoomsException = new IllegalArgumentException("availableRooms must not be null");
    private static final Throwable nullBookingsException = new IllegalArgumentException("currentBookings must not be null");
    private static final Throwable nullBufferTimeException = new IllegalArgumentException("bufferTime must not be null");

    @Test
    @DisplayName("when date is null should fail validation")
    void nullDateTest() {
        assertThatThrownBy(() -> {
            new System(null, validRooms, validBookings, validBufferTime);
        }).hasSameClassAs(nullDateException).hasMessage(nullDateException.getMessage());
    }

    @Test
    @DisplayName("when availableRooms is null should fail validation")
    void nullRoomsTest() {
        assertThatThrownBy(() -> {
            new System(validDate, null, validBookings, validBufferTime);
        }).hasSameClassAs(nullRoomsException).hasMessage(nullRoomsException.getMessage());
    }

    @Test
    @DisplayName("when currentBookings is null should fail validation")
    void nullBookingsTest() {
        assertThatThrownBy(() -> {
            new System(validDate, validRooms, null, validBufferTime);
        }).hasSameClassAs(nullBookingsException).hasMessage(nullBookingsException.getMessage());
    }

    @Test
    @DisplayName("when bufferTime is null should fail validation")
    void nullBufferTimeTest() {
        assertThatThrownBy(() -> {
            new System(validDate, validRooms, validBookings, null);
        }).hasSameClassAs(nullBufferTimeException).hasMessage(nullBufferTimeException.getMessage());
    }
}
