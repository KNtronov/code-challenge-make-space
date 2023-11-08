package com.kntronov.makespace.domain;


import java.util.List;

import static com.kntronov.makespace.domain.validation.Validations.validateNotNull;

/**
 * Snapshot of the system with available rooms and active bookings
 *
 * @param availableRooms  available rooms in the system
 * @param currentBookings currently active bookings
 */
public record System(
        List<Room> availableRooms,
        List<Booking> currentBookings
) {
    public System {
        validateNotNull("availableRooms", availableRooms);
        validateNotNull("currentBookings", currentBookings);
    }
}
