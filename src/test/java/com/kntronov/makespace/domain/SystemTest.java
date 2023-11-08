package com.kntronov.makespace.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("System Test")
class SystemTest {

    private static final Room room1 = new Room("room1", 10);
    private static final List<Room> validRooms = List.of(
            room1
    );
    private static final List<Booking> validBookings = List.of(
            new Booking(
                    room1,
                    new Booking.TimeSlot(
                            LocalDate.of(2020, 12, 10),
                            LocalTime.of(14, 0, 0),
                            LocalTime.of(14, 30, 0)
                    ),
                    8
            )
    );

    private static final Throwable nullRoomsException = new IllegalArgumentException("availableRooms must not be null");
    private static final Throwable nullBookingsException = new IllegalArgumentException("currentBookings must not be null");

    @Test
    @DisplayName("when availableRooms is null should fail validation")
    void nullRoomsTest() {
        assertThatThrownBy(() -> {
            new System(null, validBookings);
        }).hasSameClassAs(nullRoomsException).hasMessage(nullRoomsException.getMessage());
    }

    @Test
    @DisplayName("when currentBookings is null should fail validation")
    void nullBookingsTest() {
        assertThatThrownBy(() -> {
            new System(validRooms, null);
        }).hasSameClassAs(nullBookingsException).hasMessage(nullBookingsException.getMessage());
    }
}
