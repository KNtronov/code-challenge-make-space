package com.kntronov.makespace.domain.repositories;

import com.kntronov.makespace.domain.entities.SystemState;

import java.time.LocalDate;

/**
 * Repository of the SystemState aggregate entity.
 */
public interface SystemStateRepository {

    /**
     * Retrieve a system state for a given date.
     *
     * @param date date
     * @return system state
     */
    SystemState findByDate(LocalDate date);
}
