package com.kntronov.makespace.domain.repositories;

import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.entities.SystemState;

import java.time.LocalDate;
import java.util.List;

public class Mocks {

    private Mocks() {
    }

    public static abstract class SystemStateRepositoryMock implements SystemStateRepository {
        @Override
        public SystemState findByDate(LocalDate date) {
            return null;
        }
    }

    public static abstract class BookingRepositoryMock implements BookingRepository {

        @Override
        public List<Booking> findByDate(LocalDate date) {
            return null;
        }

        @Override
        public void save(Booking booking) {
        }
    }

}
