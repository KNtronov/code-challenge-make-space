package com.kntronov.makespace.domain.services;

import java.util.UUID;

/**
 * A lean service used to generate UUIDs.
 */
public interface UUIDProvider {
    /**
     * Generate a UUID.
     *
     * @return generated UUID.
     */
    UUID generateUuid();
}
