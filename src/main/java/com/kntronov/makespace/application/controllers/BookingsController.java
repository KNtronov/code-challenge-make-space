package com.kntronov.makespace.application.controllers;

import com.kntronov.makespace.application.errors.HttpError;
import com.kntronov.makespace.application.schema.BookingResponse;
import com.kntronov.makespace.application.schema.BookingsListResponse;
import com.kntronov.makespace.application.schema.CreateBookingRequest;
import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.entities.TimeSlot;
import com.kntronov.makespace.domain.errors.NoRoomAvailableException;
import com.kntronov.makespace.domain.errors.RoomNotFoundException;
import com.kntronov.makespace.domain.services.BookingService;
import com.kntronov.makespace.util.Nothing;
import com.kntronov.makespace.util.Result;

import java.time.LocalDate;
import java.util.UUID;

public class BookingsController {

    private final BookingService bookingService;

    public BookingsController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public BookingsListResponse getAllBookings(LocalDate date) {
        return new BookingsListResponse(bookingService.getAllBookingsByDate(date).stream().map(BookingResponse::fromDomainEntity).toList());
    }

    public BookingResponse getBooking(UUID id) {
        return switch (bookingService.getBooking(id)) {
            case Result.Success<Booking> success -> BookingResponse.fromDomainEntity(success.value());
            case Result.Failure<Booking> failure -> {
                switch (failure.error()) {
                    case RoomNotFoundException ignored ->
                            throw new HttpError.NoContentException("room " + id + " does not exist");
                    default -> throw new HttpError.InternalServerErrorException();
                }
            }
        };
    }

    public Nothing deleteBooking(UUID id) {
        return switch (bookingService.deleteBooking(id)) {
            case Result.Success<Nothing> ignored -> new Nothing();
            case Result.Failure<Nothing> failure -> {
                switch (failure.error()) {
                    case RoomNotFoundException ignored ->
                            throw new HttpError.NoContentException("room " + id + " does not exist");
                    default -> throw new HttpError.InternalServerErrorException();
                }
            }
        };
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
