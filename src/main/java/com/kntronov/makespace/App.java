package com.kntronov.makespace;

import com.kntronov.makespace.infrastructure.persistence.Migrations;

public class App {
    public static void main(String[] args) {
        Migrations.performMigrations();
    }
}
