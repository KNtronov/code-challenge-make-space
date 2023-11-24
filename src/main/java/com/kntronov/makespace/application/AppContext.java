package com.kntronov.makespace.application;

import com.kntronov.makespace.application.controllers.BookingsController;
import com.kntronov.makespace.application.controllers.RoomsController;
import com.kntronov.makespace.config.AppConfig;
import com.kntronov.makespace.domain.services.impl.BookingServiceImpl;
import com.kntronov.makespace.domain.services.impl.UUIDProviderImpl;
import com.kntronov.makespace.infrastructure.db.PooledDataSource;
import com.kntronov.makespace.infrastructure.repositories.BookingRepositoryImpl;
import com.kntronov.makespace.infrastructure.repositories.SystemStateRepositoryImpl;
import com.zaxxer.hikari.HikariConfig;

/**
 * Holds application facing dependencies.
 */
public record AppContext(
        PooledDataSource dataSource,
        BookingsController bookingsController,
        RoomsController roomsController
) {

    /**
     * Create and wire default dependencies to be used in a normal app execution
     *
     * @param config configuration
     * @return application context with default dependencies
     */
    public static AppContext createDefault(AppConfig config) {
        final var dataSource = setUpDatabaseDataSource(config.dbConfig());

        final var uuidProvider = new UUIDProviderImpl();

        final var bookingRepository = new BookingRepositoryImpl(dataSource);
        final var systemStateRepository = new SystemStateRepositoryImpl(dataSource, bookingRepository);

        final var bookingService = new BookingServiceImpl(uuidProvider, systemStateRepository, bookingRepository);

        final var bookingsController = new BookingsController(bookingService);
        final var roomsController = new RoomsController(bookingService);

        return new AppContext(dataSource, bookingsController, roomsController);
    }

    private static PooledDataSource setUpDatabaseDataSource(AppConfig.DBConfig config) {
        final var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.url());
        hikariConfig.setUsername(config.username());
        hikariConfig.setPassword(config.password());
        return new PooledDataSource(hikariConfig);
    }
}
