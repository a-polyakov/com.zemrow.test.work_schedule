package com.zemrow.test.work_schedule.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.zemrow.test.work_schedule.entity.DbVersion;

/**
 * DAO (data access object) для работы с версией схемы БД
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class DbVersionDao extends AbstractDao<DbVersion> {
    public static final String TABLE = "DB_VERSION";

    @Override protected String getTableName() {
        return TABLE;
    }

    @Override protected String[] getColumns() {
        return new String[] {ID};
    }

    @Override protected DbVersion read(ResultSet resultSet) throws SQLException {
        final DbVersion result = new DbVersion();
        result.setId(resultSet.getLong(1));
        return result;
    }

    @Override protected void write(PreparedStatement statement, DbVersion entity) throws SQLException {
        statement.setLong(1, entity.getId());
    }

    @Override protected void writeWithoutId(PreparedStatement statement, DbVersion entity) throws SQLException {
        throw new RuntimeException("id required");
    }

    public long getCurrentVersion(final Connection connection) throws SQLException {
        try (final Statement statement = connection.createStatement()) {
            final ResultSet resultSet = statement.executeQuery("select max(" + ID + ") from " + TABLE);
            long result = 0;
            if (resultSet.next()) {
                result = resultSet.getLong(1);
            }
            return result;
        }
    }

    public void insert(Connection connection, Long version) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement("insert into " + getTableName() + " (" + ID + ") VALUES (?)")) {
            statement.setLong(1, version);
            statement.execute();
        }
    }
}
