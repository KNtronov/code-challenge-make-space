package com.kntronov.makespace.domain.services.impl;

import com.kntronov.makespace.domain.services.UUIDProvider;

import java.util.UUID;

/**
 * Implementation of the UUIDProvider that calls Java's UUID generator.
 */
public class UUIDProviderImpl implements UUIDProvider {

    @Override
    public UUID generateUuid() {
        return UUID.randomUUID();
    }

}
