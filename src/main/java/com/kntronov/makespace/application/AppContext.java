package com.kntronov.makespace.application;

import com.kntronov.makespace.application.controllers.BookingsController;
import com.kntronov.makespace.application.controllers.RoomsController;
import com.kntronov.makespace.config.AppConfig;
import com.kntronov.makespace.domain.repositories.BookingRepository;
import com.kntronov.makespace.domain.repositories.SystemStateRepository;
import com.kntronov.makespace.domain.services.BookingService;
import com.kntronov.makespace.domain.services.UUIDProvider;
import com.kntronov.makespace.domain.services.impl.BookingServiceImpl;
import com.kntronov.makespace.domain.services.impl.UUIDProviderImpl;
import com.kntronov.makespace.infrastructure.db.PooledDataSource;
import com.kntronov.makespace.infrastructure.repositories.BookingRepositoryImpl;
import com.kntronov.makespace.infrastructure.repositories.SystemStateRepositoryImpl;
import com.zaxxer.hikari.HikariConfig;

/**
 * Instantiates, holds and wires application's dependencies.
 */
public class AppContext {

    private final PooledDataSource dataSource;

    private final BookingRepository bookingRepository;
    private final SystemStateRepository systemStateRepository;

    private final UUIDProvider uuidProvider;
    private final BookingService bookingService;

    private final BookingsController bookingsController;
    private final RoomsController roomsController;

    public AppContext(AppConfig config) {
        this.dataSource = setUpDatabaseDataSource(config.dbConfig());

        this.uuidProvider = new UUIDProviderImpl();

        this.bookingRepository = new BookingRepositoryImpl(dataSource);
        this.systemStateRepository = new SystemStateRepositoryImpl(dataSource, bookingRepository);

        this.bookingService = new BookingServiceImpl(uuidProvider, systemStateRepository, bookingRepository);

        this.bookingsController = new BookingsController(bookingService);
        this.roomsController = new RoomsController(bookingService);
    }

    private PooledDataSource setUpDatabaseDataSource(AppConfig.DBConfig config) {
        final var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.url());
        hikariConfig.setUsername(config.username());
        hikariConfig.setPassword(config.password());
        return new PooledDataSource(hikariConfig);
    }

    public PooledDataSource getDataSource() {
        return dataSource;
    }

    public BookingRepository getBookingRepository() {
        return bookingRepository;
    }

    public SystemStateRepository getSystemStateRepository() {
        return systemStateRepository;
    }

    public BookingService getBookingService() {
        return bookingService;
    }

    public BookingsController getBookingsController() {
        return bookingsController;
    }

    public RoomsController getRoomsController() {
        return roomsController;
    }
}
