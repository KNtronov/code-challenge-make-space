package com.kntronov.makespace.domain.repositories;

import com.kntronov.makespace.domain.entities.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingRepository {

    Booking find(UUID id);

    List<Booking> findByDate(LocalDate date);

    Booking delete(UUID id);

    Booking save(Booking booking);

    Booking update(Booking booking);
}
