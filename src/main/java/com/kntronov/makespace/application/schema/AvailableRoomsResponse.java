package com.kntronov.makespace.application.schema;

import java.util.List;

public record AvailableRoomsResponse(
        List<RoomResponse> availableRooms
) {
}
