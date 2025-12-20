package com.healthcare.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides a singleton HikariCP DataSource configured from environment variables.
 * Enforces environment-aware policy: H2 only for dev; prod requires explicit JDBC config.
 */
public final class DataSourceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceProvider.class);

    private static final String ENV_URL = System.getenv().getOrDefault("JDBC_URL", "");
    private static final String ENV_USER = System.getenv().getOrDefault("JDBC_USER", "");
    private static final String ENV_PASSWORD = System.getenv().getOrDefault("JDBC_PASSWORD", "");
    private static final String ENV_MAX_POOL = System.getenv().getOrDefault("JDBC_MAX_POOL", "10");
    private static final String APP_ENV = System.getenv().getOrDefault("APP_ENV", "dev");

    private static final HikariDataSource DATA_SOURCE = createDataSource();

    private DataSourceProvider() {
    }

    private static HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        boolean prod = "prod".equalsIgnoreCase(APP_ENV);
        if ((ENV_URL == null || ENV_URL.isEmpty())) {
            if (prod) {
                throw new IllegalStateException("JDBC_URL is required in production (APP_ENV=prod)");
            }
            LOGGER.warn("JDBC_URL not set; using in-memory H2 (dev mode). Data will not persist across restarts.");
            config.setJdbcUrl("jdbc:h2:mem:healthcare;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;CASE_INSENSITIVE_IDENTIFIERS=TRUE");
            config.setUsername("sa");
            config.setPassword("");
            config.setDriverClassName("org.h2.Driver");
        } else {
            config.setJdbcUrl(ENV_URL);
            config.setUsername(ENV_USER);
            config.setPassword(ENV_PASSWORD);
        }
        config.setMaximumPoolSize(Integer.parseInt(ENV_MAX_POOL));
        config.setPoolName("healthcare-pool");
        config.setAutoCommit(false);
        return new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }
}

