package com.kntronov.makespace.application.controllers;

import com.kntronov.makespace.application.errors.HttpError;
import com.kntronov.makespace.application.schema.BookingResponse;
import com.kntronov.makespace.application.schema.BookingsListResponse;
import com.kntronov.makespace.application.schema.CreateBookingRequest;
import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.entities.TimeSlot;
import com.kntronov.makespace.domain.errors.NoRoomAvailableException;
import com.kntronov.makespace.domain.services.BookingService;
import com.kntronov.makespace.util.Result;

import java.time.LocalDate;

public class BookingsController {

    private final BookingService bookingService;

    public BookingsController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public BookingsListResponse getAllBookings(LocalDate date) {
        return new BookingsListResponse(bookingService.getAllBookingsByDate(date).stream().map(BookingResponse::fromDomainEntity).toList());
    }

    public BookingResponse bookNextAvailableRoom(CreateBookingRequest request) {
        final var saveResult = bookingService.bookNextAvailableRoom(
                request.date(),
                new TimeSlot(
                        request.timeSlotStart(),
                        request.timeSlotEnd()
                ),
                request.numPeople()
        );
        return switch (saveResult) {
            case Result.Success<Booking> success -> BookingResponse.fromDomainEntity(success.value());
            case Result.Failure<Booking> failure -> {
                switch (failure.error()) {
                    case NoRoomAvailableException ignored ->
                            throw new HttpError.NoContentException("no room availability found");
                    default -> throw new HttpError.InternalServerErrorException();
                }
            }
        };
    }
}
