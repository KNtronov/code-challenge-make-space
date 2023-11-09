package com.kntronov.makespace.infrastructure.repositories;

import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.repositories.BookingRepository;
import com.kntronov.makespace.infrastructure.common.BookingMapper;
import com.kntronov.makespace.infrastructure.db.PooledDataSource;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingRepositoryImpl implements BookingRepository {

    private final PooledDataSource dataSource;

    public BookingRepositoryImpl(PooledDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Booking find(UUID id) {
        final var sql = """                    
                SELECT b.id, b.date, b.start, b."end", b.room_name, b.num_people, r.people_capacity
                FROM booking b, room r
                WHERE b.room_name = r.name
                AND b.id = ?
                """;
        return dataSource.getLeanConnection().use(c -> {
            try (final var statement = c.prepareStatement(sql)) {
                statement.setObject(1, id);
                final var result = statement.executeQuery();
                return BookingMapper.fromResult(result);
            }
        });
    }

    @Override
    public List<Booking> findByDate(LocalDate date) {
        final var sql = """
                SELECT b.id, b.date, b.start, b."end", b.room_name, b.num_people, r.people_capacity
                FROM booking b, room r
                WHERE b.room_name = r.name
                AND b.date = ?
                """;
        return dataSource.getLeanConnection().use(c -> {
            try (final var statement = c.prepareStatement(sql)) {
                statement.setDate(1, Date.valueOf(date));
                final var result = statement.executeQuery();
                final var bookings = new ArrayList<Booking>();
                while (result.next()) {
                    var booking = BookingMapper.fromResult(result);
                    bookings.add(booking);
                }
                return bookings;
            }
        });
    }

    @Override
    public Booking delete(UUID id) {
        final var sql = """                    
                DELETE
                FROM booking b
                WHERE b.id = ?
                """;
        return dataSource.getLeanConnection().transact(c -> {
            try (final var statement = c.prepareStatement(sql)) {
                statement.setObject(1, id);
                final var result = statement.executeQuery();
                return BookingMapper.fromResult(result);
            }
        });
    }

    @Override
    public Booking save(Booking booking) {
        final var sql = """
                INSERT INTO booking
                VALUES(?, ?, ?, ?, ?, ?)
                """;
        return dataSource.getLeanConnection().transact(c -> {
            final var statement = c.prepareStatement(sql);
            statement.setObject(1, booking.id());
            statement.setDate(2, Date.valueOf(booking.date()));
            statement.setTime(3, Time.valueOf(booking.timeSlot().start()));
            statement.setTime(4, Time.valueOf(booking.timeSlot().end()));
            statement.setString(5, booking.room().name());
            statement.setInt(6, booking.numPeople());
            statement.executeUpdate();
            return booking;
        });
    }

    @Override
    public Booking update(Booking booking) {
        final var sql = """
                UPDATE booking
                SET date = ?,
                start = ?,
                "end" = ?,
                room_name = ?,
                num_people = ?
                WHERE id = ?
                """;
        return dataSource.getLeanConnection().transact(c -> {
            final var statement = c.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(booking.date()));
            statement.setTime(2, Time.valueOf(booking.timeSlot().start()));
            statement.setTime(3, Time.valueOf(booking.timeSlot().end()));
            statement.setString(4, booking.room().name());
            statement.setInt(5, booking.numPeople());
            statement.setObject(6, booking.id().toString());
            statement.executeUpdate();
            return booking;
        });
    }

}
