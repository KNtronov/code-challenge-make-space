package com.kntronov.makespace;

import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.entities.Room;
import com.kntronov.makespace.domain.entities.TimeSlot;
import com.kntronov.makespace.infrastructure.db.PooledDataSource;
import com.kntronov.makespace.infrastructure.repositories.BookingRepositoryImpl;
import com.zaxxer.hikari.HikariConfig;
import org.flywaydb.core.Flyway;

import java.time.LocalDate;
import java.time.LocalTime;

public class App {
    public static void main(String[] args) {
        var dataSource = setUpDatabaseDataSource();
        runMigrations(dataSource);
        var r = new BookingRepositoryImpl(dataSource);
        r.save(new Booking(
                LocalDate.now(),
                new TimeSlot(
                        LocalTime.of(12, 0),
                        LocalTime.of(12, 30)
                ),
                new Room("C-Cave", 2),
                2
        ));
    }

    private static PooledDataSource setUpDatabaseDataSource() {
        final var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(System.getenv("POSTGRES_URL"));
        hikariConfig.setUsername(System.getenv("POSTGRES_USERNAME"));
        hikariConfig.setPassword(System.getenv("POSTGRES_PASSWORD"));
        return new PooledDataSource(hikariConfig);
    }

    private static void runMigrations(PooledDataSource pooledDataSource) {
        var flyway =
                Flyway.configure()
                        .dataSource(pooledDataSource.getDataSource())
                        .locations("db/migration")
                        .load();
        flyway.migrate();
    }
}
