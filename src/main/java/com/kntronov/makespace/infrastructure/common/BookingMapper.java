package com.kntronov.makespace.infrastructure.common;

import com.kntronov.makespace.domain.entities.Booking;
import com.kntronov.makespace.domain.entities.Room;
import com.kntronov.makespace.domain.entities.TimeSlot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Maps domain entity Booking from DB ResultSet.
 */
public class BookingMapper {
    private BookingMapper() {
    }

    public static Booking fromResult(ResultSet result) throws SQLException {
        final var resultId = UUID.fromString(result.getString("id"));
        final var resultDate = result.getDate("date").toLocalDate();
        final var start = result.getTime("start").toLocalTime();
        final var end = result.getTime("end").toLocalTime();
        final var roomName = result.getString("room_name");
        final var roomCapacity = result.getInt("people_capacity");
        final var numPeople = result.getInt("num_people");
        return new Booking(resultId, resultDate, new TimeSlot(start, end), new Room(roomName, roomCapacity), numPeople);
    }
}
