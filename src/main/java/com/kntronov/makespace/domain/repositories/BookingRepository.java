package com.kntronov.makespace.domain.repositories;

import com.kntronov.makespace.domain.entities.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for a domain entity Booking.
 */
public interface BookingRepository {

    /**
     * Retrieve a booking by id.
     *
     * @param id UUID
     * @return booking
     */
    Optional<Booking> find(UUID id);

    /**
     * Retrieve all bookings filtered by a date.
     *
     * @param date date
     * @return list of bookings
     */
    List<Booking> findByDate(LocalDate date);

    /**
     * Delete a booking by id.
     *
     * @param id UUID
     * @return number of deleted bookings
     */
    int delete(UUID id);

    /**
     * Create a new booking.
     *
     * @param booking booking to be created
     * @return created booking.
     */
    Booking save(Booking booking);
}
