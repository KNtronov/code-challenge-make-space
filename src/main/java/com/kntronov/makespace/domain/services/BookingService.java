package com.kntronov.makespace.domain.services;

import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.entities.Room;
import com.kntronov.makespace.domain.entities.TimeSlot;
import com.kntronov.makespace.util.Nothing;
import com.kntronov.makespace.util.Result;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for retrieving and performing bookings.
 */
public interface BookingService {

    /**
     * Schedule a meeting by giving a time period and capacity requirement,
     * the most optimal room which can accommodate the number of people will be allocated.
     * <p>
     * - Success with a booked Booking will be returned in case the allocation is successful.
     * - Failure with a NoRoomAvailableError will be returned in case no booking for the given inputs
     * is possible.
     *
     * @param date      date to book meeting on
     * @param timeSlot  time slot be booked
     * @param numPeople number of people to be booked
     * @return booking result
     */
    Result<Booking> bookNextAvailableRoom(LocalDate date, TimeSlot timeSlot, int numPeople);

    /**
     * Retrieve a list of available (not booked) rooms for a given a time slot in a given date.
     * The returned list of rooms is in the ascending order of the room capacity.
     *
     * @param date     booking date
     * @param timeSlot time slot range
     * @return available rooms ordered in ascending order by capacity
     */
    List<Room> getAvailableRooms(LocalDate date, TimeSlot timeSlot);

    /**
     * Retrieve a list of bookings filtered by date.
     *
     * @param date booking date
     * @return list of filtered bookings
     */
    List<Booking> getAllBookingsByDate(LocalDate date);

    /**
     * Delete a booking by id.
     *
     * @param id UUID
     * @return Success if deletion is successful, Failure otherwise
     */
    Result<Nothing> deleteBooking(UUID id);

    /**
     * Retrieve a booking by id.
     *
     * @param id UUID
     * @return Success if retrieval is successful, Failure otherwise
     */
    Result<Booking> getBooking(UUID id);
}
