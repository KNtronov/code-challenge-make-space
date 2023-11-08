package com.kntronov.makespace.domain.repositories;

import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.entities.SystemState;

import java.time.LocalDate;

public class Mocks {

    private Mocks() {
    }

    public static class MethodNotMockedException extends RuntimeException {
        public MethodNotMockedException() {
            super("called method was not mocked");
        }
    }

    public static abstract class SystemStateRepositoryMock implements SystemStateRepository {
        @Override
        public SystemState findByDate(LocalDate date) {
            throw new MethodNotMockedException();
        }
    }

    public static abstract class BookingRepositoryMock implements BookingRepository {
        @Override
        public Booking save(Booking booking) {
            throw new MethodNotMockedException();
        }
    }

}
