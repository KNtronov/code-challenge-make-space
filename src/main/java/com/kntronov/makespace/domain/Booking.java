package com.kntronov.makespace.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.kntronov.makespace.domain.validation.Validations.validateGreaterThanZero;
import static com.kntronov.makespace.domain.validation.Validations.validateNotNull;

/**
 * Booking represents a booked timeslot of a room for a certain number of people
 *
 * @param room
 * @param timeSlot
 * @param numPeople
 */
public record Booking(
        Room room,
        TimeSlot timeSlot,
        int numPeople
) {

    public Booking {
        validateNotNull("room", room);
        validateNotNull("timeSlot", timeSlot);
        validateGreaterThanZero("numPeople", numPeople);
        validateCapacity(numPeople, room);
    }

    private void validateCapacity(int numPeople, Room room) {
        if (numPeople > room.peopleCapacity()) {
            throw new IllegalArgumentException("numPeople exceeds room capacity");
        }
    }

    /**
     * TimeSlot is a range of time measured in 15 minute increments.
     *
     * @param date  date
     * @param start time slot start time
     * @param end   time slot end time
     */
    public record TimeSlot(
            LocalDate date,
            LocalTime start,
            LocalTime end
    ) {

        private static final List<Integer> validMinutes = List.of(
                0, 15, 30, 45
        );

        public TimeSlot {
            validateNotNull("date", date);
            validateNotNull("start", start);
            validateNotNull("end", end);
            validateSlotTime(start);
            validateSlotTime(end);
            validateRange(start, end);
        }

        public static List<Integer> validMinutes() {
            return validMinutes;
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
}
