package com.kntronov.makespace.application.controllers;

import com.kntronov.makespace.application.schema.AvailableRoomsResponse;
import com.kntronov.makespace.application.schema.RoomResponse;
import com.kntronov.makespace.domain.entities.TimeSlot;
import com.kntronov.makespace.domain.services.BookingService;

import java.time.LocalDate;
import java.time.LocalTime;

public class RoomsController {

    private final BookingService bookingService;

    public RoomsController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public AvailableRoomsResponse getAvailableRooms(LocalDate date, LocalTime timeSlotStart, LocalTime timeSlotEnd) {
        return new AvailableRoomsResponse(bookingService.getAvailableRooms(date, new TimeSlot(timeSlotStart, timeSlotEnd))
                .stream()
                .map(RoomResponse::fromDomainEntity)
                .toList());
    }
}
