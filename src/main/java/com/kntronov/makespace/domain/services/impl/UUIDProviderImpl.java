package com.kntronov.makespace.domain.services.impl;

import com.kntronov.makespace.domain.services.UUIDProvider;

import java.util.UUID;

public class UUIDProviderImpl implements UUIDProvider {

    @Override
    public UUID generateUuid() {
        return UUID.randomUUID();
    }

}
