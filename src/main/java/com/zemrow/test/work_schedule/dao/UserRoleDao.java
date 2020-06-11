package com.zemrow.test.work_schedule.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.zemrow.test.work_schedule.entity.UserRole;
import com.zemrow.test.work_schedule.entity.UserRoleEnum;

/**
 * DAO (data access object) для работы с правами пользователя
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class UserRoleDao extends AbstractDao<UserRole> {
    public static final String TABLE = "user_role";

    public static final String USER_ID = "user_id";
    public static final String ROLE = "role";

    @Override protected String getTableName() {
        return TABLE;
    }

    @Override protected String[] getColumns() {
        return new String[] {
            ID, USER_ID, ROLE
        };
    }

    @Override protected UserRole read(ResultSet resultSet) throws SQLException {
        final UserRole result = new UserRole();
        result.setId(resultSet.getLong(1));
        result.setUserId(resultSet.getLong(2));
        result.setRole(UserRoleEnum.valueOf(resultSet.getString(3)));
        return result;
    }

    @Override protected void write(PreparedStatement statement, UserRole entity) throws SQLException {
        statement.setLong(1, entity.getId());
        statement.setLong(2, entity.getUserId());
        statement.setString(3, entity.getRole().name());
    }

    @Override protected void writeWithoutId(PreparedStatement statement, UserRole entity) throws SQLException {
        statement.setLong(1, entity.getUserId());
        statement.setString(2, entity.getRole().name());
    }
}
