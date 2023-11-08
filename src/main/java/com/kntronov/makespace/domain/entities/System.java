package com.kntronov.makespace.domain.entities;


import java.time.LocalDate;
import java.util.List;

import static com.kntronov.makespace.domain.entities.validation.Validations.validateNotNull;

/**
 * Snapshot of the system for a date with available rooms, active bookings and buffer time.
 *
 * @param date            date of the snapshot
 * @param availableRooms  available rooms in the system
 * @param currentBookings currently active bookings
 * @param bufferTime      buffer time range wh
 */
public record System(
        LocalDate date,
        List<Room> availableRooms,
        List<Booking> currentBookings,
        TimeSlot bufferTime
) {
    public System {
        validateNotNull("date", date);
        validateNotNull("availableRooms", availableRooms);
        validateNotNull("currentBookings", currentBookings);
        validateNotNull("bufferTime", bufferTime);
    }
}
