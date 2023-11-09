package com.kntronov.makespace.testing;

import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.entities.SystemState;
import com.kntronov.makespace.domain.repositories.BookingRepository;
import com.kntronov.makespace.domain.repositories.SystemStateRepository;
import com.kntronov.makespace.domain.services.UUIDProvider;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        public Optional<Booking> find(UUID id) {
            throw new MethodNotMockedException();
        }

        @Override
        public List<Booking> findByDate(LocalDate date) {
            throw new MethodNotMockedException();
        }

        @Override
        public int delete(UUID id) {
            throw new MethodNotMockedException();
        }

        @Override
        public Booking save(Booking booking) {
            throw new MethodNotMockedException();
        }
    }

    public static class UUIDProviderMock implements UUIDProvider {

        private final List<UUID> returnedUuidSequence;
        private int pointer = 0;

        public UUIDProviderMock(List<UUID> returnedUuidSequence) {
            this.returnedUuidSequence = returnedUuidSequence;
        }

        @Override
        public UUID generateUuid() {
            if (!returnedUuidSequence.isEmpty()) {
                var result = returnedUuidSequence.get(pointer);
                pointer = (pointer + 1) % returnedUuidSequence.size();
                return result;
            } else {
                throw new MethodNotMockedException();
            }
        }
    }

}
