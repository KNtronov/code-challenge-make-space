package com.kntronov.makespace.domain.entities;

import java.time.LocalTime;
import java.util.List;

import static com.kntronov.makespace.domain.entities.validation.Validations.validateNotNull;

/**
 * TimeSlot is a range of time measured in 15 minute increments [0, 15, 30, 45].
 *
 * @param start time slot start time in 15 minute increments [0, 15, 30, 45]
 * @param end   time slot end time in 15 minute increments [0, 15, 30, 45]
 */
public record TimeSlot(
        LocalTime start,
        LocalTime end
) {

    private static final List<Integer> validMinutes = List.of(
            0, 15, 30, 45
    );

    public TimeSlot {
        validateNotNull("start", start);
        validateNotNull("end", end);
        validateSlotTime(start);
        validateSlotTime(end);
        validateRange(start, end);
    }

    private void validateSlotTime(LocalTime slot) {
        if (slot.getSecond() != 0 || slot.getNano() != 0) {
            throw new IllegalArgumentException("slot seconds and nano must be 0");
        }
        if (!validMinutes.contains(slot.getMinute())) {
            throw new IllegalArgumentException("slot minutes must be one of " + validMinutes);
        }
    }

    private void validateRange(LocalTime start, LocalTime end) {
        if (end.isBefore(start) || start.equals(end)) {
            throw new IllegalArgumentException("end must be after start");
        }
    }
}
