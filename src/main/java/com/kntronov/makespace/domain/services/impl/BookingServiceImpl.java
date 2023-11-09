package com.kntronov.makespace.domain.services.impl;

import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.entities.Room;
import com.kntronov.makespace.domain.entities.TimeSlot;
import com.kntronov.makespace.domain.errors.NoRoomAvailableException;
import com.kntronov.makespace.domain.errors.RoomNotFoundException;
import com.kntronov.makespace.domain.repositories.BookingRepository;
import com.kntronov.makespace.domain.repositories.SystemStateRepository;
import com.kntronov.makespace.domain.services.BookingService;
import com.kntronov.makespace.domain.services.UUIDProvider;
import com.kntronov.makespace.util.Nothing;
import com.kntronov.makespace.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger("BookingService");

    private final UUIDProvider uuidProvider;

    private final SystemStateRepository systemStateRepository;
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(UUIDProvider uuidProvider, SystemStateRepository systemStateRepository, BookingRepository bookingRepository) {
        this.uuidProvider = uuidProvider;
        this.systemStateRepository = systemStateRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Result<Booking> bookNextAvailableRoom(LocalDate date, TimeSlot timeSlot, int numPeople) {
        logger.info("searching for available rooms for date: {}, timeSlot: {}, numPeople: {}", date, timeSlot, numPeople);
        final var maybeAvailableRoom = getAvailableRooms(date, timeSlot)
                .stream()
                .filter(room -> room.peopleCapacity() >= numPeople)
                .min(Comparator.comparing(Room::peopleCapacity));
        if (maybeAvailableRoom.isPresent()) {
            final var room = maybeAvailableRoom.get();
            logger.info("found available room {}", maybeAvailableRoom.get());
            final var booking = new Booking(
                    uuidProvider.generateUuid(),
                    date,
                    timeSlot,
                    room,
                    numPeople
            );
            final var savedBooking = bookingRepository.save(booking);
            logger.info("booking {} successfully created", savedBooking);
            return new Result.Success<>(savedBooking);
        } else {
            logger.info("no booking found");
            return new Result.Failure<>(new NoRoomAvailableException());
        }
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate date, TimeSlot timeSlot) {
        logger.info("searching for available rooms for date: {} timeSlot: {}", date, timeSlot);
        final var system = systemStateRepository.findByDate(date);
        logger.info("system state: {}", system);
        final var rooms = system.availableRooms();
        final var roomsWithBookingsForTimeSlot = system.currentBookings()
                .stream()
                .filter(booking -> booking.timeSlot().isOverlapping(timeSlot))
                .map(Booking::room)
                .toList();
        final var result = rooms.stream()
                .filter(ignored -> system.bufferTimes().stream().noneMatch(timeSlot::isOverlapping))
                .filter(room -> !roomsWithBookingsForTimeSlot.contains(room))
                .sorted(Comparator.comparing(Room::peopleCapacity))
                .toList();
        logger.info("available rooms: {}", result);
        return result;
    }

    @Override
    public List<Booking> getAllBookingsByDate(LocalDate date) {
        return bookingRepository.findByDate(date);
    }

    @Override
    public Result<Nothing> deleteBooking(UUID id) {
        return switch (getBooking(id)) {
            case Result.Success<Booking> ignored -> {
                bookingRepository.delete(id);
                yield Result.pure(new Nothing());
            }
            case Result.Failure<Booking> failure -> Result.fail(failure.error());
        };
    }

    @Override
    public Result<Booking> getBooking(UUID id) {
        return bookingRepository.find(id)
                .map(Result::pure)
                .orElse(Result.fail(new RoomNotFoundException()));
    }
}
