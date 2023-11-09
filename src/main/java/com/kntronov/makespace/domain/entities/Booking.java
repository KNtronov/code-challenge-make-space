package com.kntronov.makespace.domain.entities;

import java.time.LocalDate;
import java.util.UUID;

import static com.kntronov.makespace.domain.entities.validation.Validations.validateGreaterThanZero;
import static com.kntronov.makespace.domain.entities.validation.Validations.validateNotNull;

/**
 * Booking represents a booked timeslot of a room for a certain number of people
 *
 * @param room      booked room
 * @param timeSlot  booked time slot
 * @param numPeople number of people booked for
 */
public record Booking(
        UUID id,
        LocalDate date,
        TimeSlot timeSlot,
        Room room,
        int numPeople
) {

    public Booking {
        validateNotNull("id", id);
        validateNotNull("date", date);
        validateNotNull("timeSlot", timeSlot);
        validateNotNull("room", room);
        validateGreaterThanZero("numPeople", numPeople);
        validateCapacity(numPeople, room);
    }

    private void validateCapacity(int numPeople, Room room) {
        if (numPeople > room.peopleCapacity()) {
            throw new IllegalArgumentException("numPeople exceeds room capacity");
        }
    }
}
