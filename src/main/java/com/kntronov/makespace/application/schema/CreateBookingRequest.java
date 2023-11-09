package com.kntronov.makespace.application.schema;

import com.kntronov.makespace.domain.entities.TimeSlot;
import io.javalin.http.Context;
import io.javalin.validation.BodyValidator;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateBookingRequest(
        LocalDate date,
        LocalTime timeSlotStart,
        LocalTime timeSlotEnd,
        int numPeople
) {
    public static BodyValidator<CreateBookingRequest> validated(Context ctx) {
        return ctx.bodyValidator(CreateBookingRequest.class)
                .check(req -> req.date() != null, "date is mandatory")
                .check(req -> req.timeSlotStart() != null, "timeSlotStart is mandatory")
                .check(req -> req.timeSlotEnd() != null, "timeSlotEnd is mandatory")
                .check(req -> req.numPeople() > 0, "numPeople must be > 0")
                .check(req -> req.timeSlotStart() == null || req.timeSlotEnd() == null || req.timeSlotEnd().isAfter(req.timeSlotStart()), "timeSlotEnd must be after timeSlotStart")
                .check(req -> req.timeSlotStart() == null || TimeSlot.getValidMinutes().contains(req.timeSlotStart().getMinute()), "timeSlotStart minutes must be in " + TimeSlot.getValidMinutes())
                .check(req -> req.timeSlotEnd() == null || TimeSlot.getValidMinutes().contains(req.timeSlotEnd().getMinute()), "timeSlotEnd minutes must be in " + TimeSlot.getValidMinutes());
    }
}
