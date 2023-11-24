package com.kntronov.makespace.application;

import com.kntronov.makespace.application.AppContext;
import com.kntronov.makespace.application.JavalinApp;
import com.kntronov.makespace.application.controllers.BookingsController;
import com.kntronov.makespace.application.controllers.RoomsController;
import com.kntronov.makespace.config.AppConfig;
import com.kntronov.makespace.domain.services.BookingService;
import com.kntronov.makespace.infrastructure.db.PooledDataSource;
import com.kntronov.makespace.testing.Mocks;
import io.javalin.Javalin;

public class JavalinTestApp {

    private static final AppConfig.ServerConfig testConfig = new AppConfig.ServerConfig(8080);
    private final Javalin subject = new JavalinApp(new AppContext(dataSource(), bookingsController(), roomsController()), testConfig).getJavalin();

    protected BookingService bookingService() {
        return new Mocks.BookingServiceMock() {

        };
    }

    public Javalin subject() {
        return this.subject;
    }

    private PooledDataSource dataSource() {
        return null;
    }

    private BookingsController bookingsController() {
        return new BookingsController(bookingService());
    }

    private RoomsController roomsController() {
        return new RoomsController(bookingService());
    }
}
