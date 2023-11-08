package com.kntronov.makespace.domain.repositories;

import com.kntronov.makespace.domain.entities.Booking;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository {
    List<Booking> findByDate(LocalDate date);

    void save(Booking booking);
}
