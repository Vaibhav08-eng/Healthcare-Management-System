package com.healthcare.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.healthcare.util.DataSourceProvider;

/**
 * @deprecated Use {@link DataSourceProvider#getConnection()} instead.
 */
@Deprecated

/**
 * Deprecated: Use {@link DataSourceProvider#getConnection()} instead.
 */
public final class ConnectionFactory {

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        return DataSourceProvider.getConnection();
    }
}

