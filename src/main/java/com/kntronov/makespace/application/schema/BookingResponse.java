package com.kntronov.makespace.application.schema;

import com.kntronov.makespace.domain.entities.Booking;

import java.time.LocalDate;

/**
 * Booking represents a booked timeslot of a room for a certain number of people
 *
 * @param room      booked room
 * @param timeSlot  booked time slot
 * @param numPeople number of people booked for
 */
public record BookingResponse(
        LocalDate date,
        TimeSlotResponse timeSlot,
        RoomResponse room,
        int numPeople
) {

    public static BookingResponse fromDomainEntity(Booking booking) {
        return new BookingResponse(
                booking.date(),
                TimeSlotResponse.fromDomainEntity(booking.timeSlot()),
                RoomResponse.fromDomainEntity(booking.room()),
                booking.numPeople()
        );
    }

    public Booking toDomainEntity() {
        return new Booking(
                this.date,
                this.timeSlot().toDomainEntity(),
                this.room().toDomainEntity(),
                this.numPeople
        );
    }

}
