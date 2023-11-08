package com.kntronov.makespace.infrastructure.db;

import com.kntronov.makespace.infrastructure.errors.UncheckedSQLException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class LeanConnection {

    private final Connection connection;

    public LeanConnection(DataSource dataSource) {
        try {
            this.connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    public <T> T use(ConnectionConsumer<T> executable) {
        try {
            return executable.apply(connection);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    public <T> T transact(ConnectionConsumer<T> executable) {
        try {
            connection.setAutoCommit(false);
            var result = executable.apply(connection);
            connection.commit();
            return result;
        } catch (Exception e1) {
            try {
                connection.rollback();
            } catch (SQLException e2) {
                throw new UncheckedSQLException(e2);
            }
            throw new UncheckedSQLException(e1);
        }
    }

    @FunctionalInterface
    public interface ConnectionConsumer<T> {
        T apply(Connection c) throws SQLException;
    }
}
