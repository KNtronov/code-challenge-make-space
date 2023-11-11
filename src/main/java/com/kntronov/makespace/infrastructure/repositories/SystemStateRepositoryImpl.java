package com.kntronov.makespace.infrastructure.repositories;

import com.kntronov.makespace.domain.entities.Room;
import com.kntronov.makespace.domain.entities.SystemState;
import com.kntronov.makespace.domain.entities.TimeSlot;
import com.kntronov.makespace.domain.repositories.BookingRepository;
import com.kntronov.makespace.domain.repositories.SystemStateRepository;
import com.kntronov.makespace.infrastructure.db.PooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the SystemStateRepositoryImpl interface that persists and reads data from a SQL database.
 */
public class SystemStateRepositoryImpl implements SystemStateRepository {

    private final PooledDataSource dataSource;

    private final BookingRepository bookingRepository;

    public SystemStateRepositoryImpl(PooledDataSource dataSource, BookingRepository bookingRepository) {
        this.dataSource = dataSource;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public SystemState findByDate(LocalDate date) {
        final var bookings = bookingRepository.findByDate(date);
        return dataSource.getLeanConnection().use(c -> {
            final var rooms = findAllRooms(c);
            final var bufferTimes = findAllBufferTimes(c);
            return new SystemState(
                    date,
                    rooms,
                    bookings,
                    bufferTimes
            );
        });
    }

    private List<Room> findAllRooms(Connection c) throws SQLException {
        final var sql = """
                SELECT name, people_capacity FROM room
                """;
        try (final var statement = c.prepareStatement(sql)) {
            final var result = statement.executeQuery();
            final var rooms = new ArrayList<Room>();
            while (result.next()) {
                final var name = result.getString("name");
                final var capacity = result.getInt("people_capacity");
                final var room = new Room(name, capacity);
                rooms.add(room);
            }
            return rooms;
        }
    }

    private List<TimeSlot> findAllBufferTimes(Connection c) throws SQLException {
        final var sql = """
                SELECT start, "end" FROM buffer_time
                """;
        try (final var statement = c.prepareStatement(sql)) {
            final var result = statement.executeQuery();
            final var timeSlots = new ArrayList<TimeSlot>();
            while (result.next()) {
                final var name = result.getTime("start").toLocalTime();
                final var capacity = result.getTime("end").toLocalTime();
                final var timeSlot = new TimeSlot(name, capacity);
                timeSlots.add(timeSlot);
            }
            return timeSlots;
        }
    }
}
