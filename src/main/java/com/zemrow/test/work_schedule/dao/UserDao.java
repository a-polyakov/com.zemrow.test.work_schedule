package com.zemrow.test.work_schedule.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.zemrow.test.work_schedule.entity.User;

/**
 * DAO (data access object) для работы с пользователями
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class UserDao extends AbstractDao<User> {

    public static final String TABLE = "all_user";
    public static final String FULL_NAME = "full_name";

    @Override protected String getTableName() {
        return TABLE;
    }

    @Override protected String[] getColumns() {
        return new String[] {
            ID, FULL_NAME
        };
    }

    @Override protected User read(ResultSet resultSet) throws SQLException {
        final User result = new User();
        result.setId(resultSet.getLong(1));
        result.setFullName(resultSet.getString(2));
        return result;
    }

    @Override protected void write(PreparedStatement statement, User entity) throws SQLException {
        statement.setLong(1, entity.getId());
        statement.setString(2, entity.getFullName());
    }

    @Override protected void writeWithoutId(PreparedStatement statement, User entity) throws SQLException {
        statement.setString(1, entity.getFullName());
    }

    /**
     * Кеш запроса
     */
    private String querySelectByFullName;

    /**
     * Найти пользователя по имени.
     * @param connection Подключение к БД.
     * @param fullName Полное наименование
     * @return Пользователь.
     * @throws SQLException
     */
    public User selectByFullName(Connection connection, String fullName) throws SQLException {
        if (querySelectByFullName == null) {
            synchronized (this) {
                if (querySelectByFullName == null) {
                    final StringBuilder sb = new StringBuilder("select ");
                    sb.append(getAllColumns());
                    sb.append(" from ");
                    sb.append(getTableName());
                    sb.append(" where ");
                    sb.append(FULL_NAME);
                    sb.append("=?");
                    querySelectByFullName = sb.toString();
                }
            }
        }
        try (final PreparedStatement statement = connection.prepareStatement(querySelectByFullName)) {
            statement.setString(1, fullName);
            final ResultSet resultSet = statement.executeQuery();
            User result = null;
            if (resultSet.next()) {
                result = read(resultSet);
            }
            return result;
        }
    }
}
