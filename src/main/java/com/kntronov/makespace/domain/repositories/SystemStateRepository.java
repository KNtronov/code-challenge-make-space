package com.kntronov.makespace.domain.repositories;

import com.kntronov.makespace.domain.entities.SystemState;

import java.time.LocalDate;

public interface SystemStateRepository {

    SystemState findByDate(LocalDate date);
}
