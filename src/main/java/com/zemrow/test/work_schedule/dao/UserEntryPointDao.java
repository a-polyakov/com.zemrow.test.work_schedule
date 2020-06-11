package com.zemrow.test.work_schedule.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.zemrow.test.work_schedule.entity.EntryPointTypeEnum;
import com.zemrow.test.work_schedule.entity.UserEntryPoint;

/**
 * DAO (data access object) для работы с авторизацией
 *
 * @author Alexandr Polyakov on 2018.08.18
 */
public class UserEntryPointDao extends AbstractDao<UserEntryPoint> {

    public static final String TABLE = "user_entry_point";
    public static final String USER_ID = "user_id";
    public static final String ENTRY_POINT_TYPE = "entry_point_type";
    public static final String CLIENT_ID = "client_id";
    public static final String CREDENTIAL = "credential";

    @Override protected String getTableName() {
        return TABLE;
    }

    @Override protected String[] getColumns() {
        return new String[] {
            ID, USER_ID, ENTRY_POINT_TYPE, CLIENT_ID, CREDENTIAL
        };
    }

    @Override protected UserEntryPoint read(ResultSet resultSet) throws SQLException {
        final UserEntryPoint result = new UserEntryPoint();
        result.setId(resultSet.getLong(1));
        result.setUserId(resultSet.getLong(2));
        result.setEntryPointType(EntryPointTypeEnum.valueOf(resultSet.getString(3)));
        result.setClientId(resultSet.getString(4));
        result.setCredential(resultSet.getString(5));
        return result;
    }

    @Override protected void write(PreparedStatement statement, UserEntryPoint entity) throws SQLException {
        statement.setLong(1, entity.getId());
        statement.setLong(2, entity.getUserId());
        statement.setString(3, entity.getEntryPointType().name());
        statement.setString(4, entity.getClientId());
        statement.setString(5, entity.getCredential());
    }

    @Override protected void writeWithoutId(PreparedStatement statement, UserEntryPoint entity) throws SQLException {
        statement.setLong(1, entity.getUserId());
        statement.setString(2, entity.getEntryPointType().name());
        statement.setString(3, entity.getClientId());
        statement.setString(4, entity.getCredential());
    }

    /**
     * Кеш запроса
     */
    private String querySelectByTypeAndClientId;

    public UserEntryPoint selectByTypeAndClientId(Connection connection, EntryPointTypeEnum entryPointType,
        String freshdeskId) throws SQLException {
        if (querySelectByTypeAndClientId == null) {
            synchronized (this) {
                if (querySelectByTypeAndClientId == null) {
                    final StringBuilder sb = new StringBuilder("select ");
                    sb.append(getAllColumns());
                    sb.append(" from ");
                    sb.append(getTableName());
                    sb.append(" where ");
                    sb.append(ENTRY_POINT_TYPE);
                    sb.append("=? and ");
                    sb.append(CLIENT_ID);
                    sb.append("=?");
                    querySelectByTypeAndClientId = sb.toString();
                }
            }
        }
        try (final PreparedStatement statement = connection.prepareStatement(querySelectByTypeAndClientId)) {
            statement.setString(1, entryPointType.name());
            statement.setString(2, freshdeskId);
            final ResultSet resultSet = statement.executeQuery();
            UserEntryPoint result = null;
            if (resultSet.next()) {
                result = read(resultSet);
            }
            return result;
        }
    }

}