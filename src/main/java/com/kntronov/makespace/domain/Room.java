package com.kntronov.makespace.domain;

import static com.kntronov.makespace.domain.validation.Validations.*;

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
