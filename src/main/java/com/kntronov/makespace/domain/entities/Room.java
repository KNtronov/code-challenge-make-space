package com.kntronov.makespace.domain.entities;

import static com.kntronov.makespace.domain.entities.validation.Validations.*;

/**
 * Room is a bookable space with a finite capacity of people
 *
 * @param name           verbose room name
 * @param peopleCapacity maximum number of people the room is able to contain
 */
public record Room(
        String name,
        int peopleCapacity
) {
    public Room {
        validateNotNull("name", name);
        validateNotBlank("name", name);
        validateGreaterThanZero("peopleCapacity", peopleCapacity);
    }
}
