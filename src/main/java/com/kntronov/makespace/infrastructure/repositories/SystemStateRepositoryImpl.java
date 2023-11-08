package com.kntronov.makespace.infrastructure.repositories;

import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.entities.Room;
import com.kntronov.makespace.domain.entities.SystemState;
import com.kntronov.makespace.domain.entities.TimeSlot;
import com.kntronov.makespace.domain.repositories.SystemStateRepository;
import com.kntronov.makespace.infrastructure.db.PooledDataSource;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SystemStateRepositoryImpl implements SystemStateRepository {

    private final PooledDataSource dataSource;

    public SystemStateRepositoryImpl(PooledDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public SystemState findByDate(LocalDate date) {
        return dataSource.getLeanConnection().use(c -> {
            final var rooms = findAllRooms(c);
            final var bookings = findBookingsByDate(c, date);
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

    private List<Booking> findBookingsByDate(Connection c, LocalDate date) throws SQLException {
        final var sql = """
                SELECT b.date, b.start, b."end", b.room_name, b.num_people, r.people_capacity
                FROM booking b, room r
                WHERE b.room_name = r.name
                AND b.date = ?
                """;
        try (final var statement = c.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(date));
            final var result = statement.executeQuery();
            final var bookings = new ArrayList<Booking>();
            while (result.next()) {
                final var resultDate = result.getDate("date").toLocalDate();
                final var start = result.getTime("start").toLocalTime();
                final var end = result.getTime("end").toLocalTime();
                final var roomName = result.getString("room_name");
                final var roomCapacity = result.getInt("people_capacity");
                final var numPeople = result.getInt("num_people");
                final var booking = new Booking(
                        resultDate,
                        new TimeSlot(
                                start,
                                end
                        ),
                        new Room(
                                roomName,
                                roomCapacity
                        ),
                        numPeople
                );
                bookings.add(booking);
            }
            return bookings;
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
