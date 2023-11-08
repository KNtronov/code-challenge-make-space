package com.kntronov.makespace.infrastructure.repositories;

import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.repositories.BookingRepository;
import com.kntronov.makespace.infrastructure.db.PooledDataSource;

import java.sql.Date;
import java.sql.Time;

public class BookingRepositoryImpl implements BookingRepository {

    private final PooledDataSource dataSource;

    public BookingRepositoryImpl(PooledDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Booking save(Booking booking) {
        return dataSource.getLeanConnection().transact(c -> {
            final var sql = """
                    INSERT INTO booking
                    VALUES(?, ?, ?, ?, ?)
                    """;
            final var statement = c.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(booking.date()));
            statement.setTime(2, Time.valueOf(booking.timeSlot().start()));
            statement.setTime(3, Time.valueOf(booking.timeSlot().end()));
            statement.setString(4, booking.room().name());
            statement.setInt(5, booking.numPeople());
            statement.executeUpdate();
            return booking;
        });
    }

}
