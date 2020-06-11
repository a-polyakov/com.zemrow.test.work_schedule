package com.zemrow.test.work_schedule;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.h2.jdbcx.JdbcConnectionPool;

/**
 * Обертка для получения соединения к БД
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class DataSourceWrapper implements Closeable {
    private final JdbcConnectionPool cp;

    public DataSourceWrapper() throws ClassNotFoundException {
        Class.forName("org.h2.Driver");
        cp = JdbcConnectionPool.create("jdbc:h2:~/work_schedule", "sa", "sa");
    }

    public Connection getConnection() throws SQLException {
        final Connection connection = cp.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }

    @Override public void close() throws IOException {
        cp.dispose();
    }
}
