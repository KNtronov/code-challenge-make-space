package com.kntronov.makespace.application.schema;

import java.util.List;

public record BookingsListResponse(List<BookingResponse> bookings) {
}
