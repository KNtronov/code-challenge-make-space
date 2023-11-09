package com.kntronov.makespace.application.schema;

import com.kntronov.makespace.domain.entities.TimeSlot;

import java.time.LocalTime;

/**
 * TimeSlot is a range of time measured in 15 minute increments [0, 15, 30, 45].
 *
 * @param start time slot start time in 15 minute increments [0, 15, 30, 45]
 * @param end   time slot end time in 15 minute increments [0, 15, 30, 45]
 */
public record TimeSlotResponse(
        LocalTime start,
        LocalTime end
) {

    public static TimeSlotResponse fromDomainEntity(TimeSlot timeSlot) {
        return new TimeSlotResponse(
                timeSlot.start(),
                timeSlot.end()
        );
    }

    public TimeSlot toDomainEntity() {
        return new TimeSlot(
                this.start,
                this.end
        );
    }

}
