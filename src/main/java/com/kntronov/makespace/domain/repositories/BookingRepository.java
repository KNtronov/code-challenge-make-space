package com.kntronov.makespace.domain.repositories;

import com.kntronov.makespace.domain.entities.Booking;

public interface BookingRepository {
    Booking save(Booking booking);
}
