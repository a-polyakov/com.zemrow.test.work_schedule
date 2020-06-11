package com.zemrow.test.work_schedule.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.zemrow.test.work_schedule.controller.dto.SessionDto;
import com.zemrow.test.work_schedule.entity.UserSession;

/**
 * DAO (data access object) для работы с сессией пользователя
 *
 * @author Alexandr Polyakov on 2018.08.18
 */
public class UserSessionDao extends AbstractDao<UserSession> {

    public static final String TABLE = "user_session";
    public static final String USER_ENTRY_POINT_ID = "user_entry_point_id";
    public static final String TOKEN = "token";
    public static final String CREATE_TIME = "create_time";

    @Override protected String getTableName() {
        return TABLE;
    }

    @Override protected String[] getColumns() {
        return new String[] {
            ID, USER_ENTRY_POINT_ID, TOKEN, CREATE_TIME
        };
    }

    @Override protected UserSession read(ResultSet resultSet) throws SQLException {
        final UserSession result = new UserSession();
        result.setId(resultSet.getLong(1));
        result.setUserEntryPointId(resultSet.getLong(2));
        result.setToken(resultSet.getString(3));
        result.setCreateTime(resultSet.getLong(4));
        return result;
    }

    @Override protected void write(PreparedStatement statement, UserSession entity) throws SQLException {
        statement.setLong(1, entity.getId());
        statement.setLong(2, entity.getUserEntryPointId());
        statement.setString(3, entity.getToken());
        statement.setLong(4, entity.getCreateTime());
    }

    @Override protected void writeWithoutId(PreparedStatement statement, UserSession entity) throws SQLException {
        statement.setLong(1, entity.getUserEntryPointId());
        statement.setString(2, entity.getToken());
        statement.setLong(3, entity.getCreateTime());
    }

    /**
     * Кеш запроса
     */
    private String querySelectByToken;

    public SessionDto selectByToken(Connection connection, String token) throws SQLException {
        if (querySelectByToken == null) {
            synchronized (this) {
                if (querySelectByToken == null) {
                    final StringBuilder sb = new StringBuilder("select ");
                    sb.append("ep." + UserEntryPointDao.USER_ID);
                    sb.append(" from ");
                    sb.append(UserEntryPointDao.TABLE + " as ep");
                    sb.append(" join ");
                    sb.append(TABLE + " as us on ep." + UserEntryPointDao.ID + "=us." + USER_ENTRY_POINT_ID);
                    sb.append(" where us.");
                    sb.append(TOKEN);
                    sb.append("=?");
                    querySelectByToken = sb.toString();
                }
            }
        }
        try (final PreparedStatement statement = connection.prepareStatement(querySelectByToken)) {
            statement.setString(1, token);
            final ResultSet resultSet = statement.executeQuery();
            SessionDto result = null;
            if (resultSet.next()) {
                result = new SessionDto();
                result.setUserId(resultSet.getLong(1));
            }
            return result;
        }
    }

}