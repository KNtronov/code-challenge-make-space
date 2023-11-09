package com.kntronov.makespace.domain.repositories;

import com.kntronov.makespace.domain.entities.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository {

    Optional<Booking> find(UUID id);

    List<Booking> findByDate(LocalDate date);

    int delete(UUID id);

    Booking save(Booking booking);
}
