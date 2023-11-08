package com.kntronov.makespace.domain.services.impl;

import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.entities.Room;
import com.kntronov.makespace.domain.entities.TimeSlot;
import com.kntronov.makespace.domain.errors.NoRoomAvailableError;
import com.kntronov.makespace.domain.repositories.BookingRepository;
import com.kntronov.makespace.domain.repositories.SystemStateRepository;
import com.kntronov.makespace.domain.services.BookingService;
import com.kntronov.makespace.util.Result;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class BookingServiceImpl implements BookingService {

    private final SystemStateRepository systemStateRepository;
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(SystemStateRepository systemStateRepository, BookingRepository bookingRepository) {
        this.systemStateRepository = systemStateRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Result<Booking> bookNextAvailableRoom(LocalDate date, TimeSlot timeSlot, int numPeople) {
        final var maybeAvailableRoom = getAvailableRooms(date, timeSlot)
                .stream()
                .filter(room -> room.peopleCapacity() >= numPeople)
                .min(Comparator.comparing(Room::peopleCapacity));
        if (maybeAvailableRoom.isPresent()) {
            final var room = maybeAvailableRoom.get();
            final var booking = new Booking(
                    date,
                    timeSlot,
                    room,
                    numPeople
            );
            final var savedBooking = bookingRepository.save(booking);
            return new Result.Success<>(savedBooking);
        } else {
            return new Result.Failure<>(new NoRoomAvailableError());
        }
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate date, TimeSlot timeSlot) {
        final var system = systemStateRepository.findByDate(date);
        final var rooms = system.availableRooms();
        final var roomsWithBookingsForTimeSlot = system.currentBookings()
                .stream()
                .filter(booking -> booking.timeSlot().isOverlapping(timeSlot))
                .map(Booking::room)
                .toList();
        return rooms.stream()
                .filter(ignored -> !timeSlot.isOverlapping(system.bufferTime()))
                .filter(room -> !roomsWithBookingsForTimeSlot.contains(room))
                .sorted(Comparator.comparing(Room::peopleCapacity))
                .toList();
    }
}
