package com.kntronov.makespace.application.schema;

import com.kntronov.makespace.domain.entities.Room;

/**
 * Room is a bookable space with a finite capacity of people
 *
 * @param name           verbose room name
 * @param peopleCapacity maximum number of people the room is able to contain
 */
public record RoomResponse(
        String name,
        int peopleCapacity
) {

    public static RoomResponse fromDomainEntity(Room room) {
        return new RoomResponse(
                room.name(),
                room.peopleCapacity()
        );
    }

    public Room toDomainEntity() {
        return new Room(
                this.name,
                this.peopleCapacity
        );
    }
}
