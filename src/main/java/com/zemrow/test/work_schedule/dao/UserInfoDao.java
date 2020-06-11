package com.zemrow.test.work_schedule.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.zemrow.test.work_schedule.entity.UserInfo;
import org.json.JSONObject;

/**
 * DAO (data access object) для работы с дополнительной информацией пользователя
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class UserInfoDao extends AbstractDao<UserInfo> {

    public static final String TABLE = "user_info";
    public static final String INFO = "info";

    @Override protected String getTableName() {
        return TABLE;
    }

    @Override protected String[] getColumns() {
        return new String[] {
            ID, INFO
        };
    }

    @Override protected UserInfo read(ResultSet resultSet) throws SQLException {
        final UserInfo result = new UserInfo();
        result.setId(resultSet.getLong(1));
        result.setInfo(new JSONObject(resultSet.getString(2)));
        return result;
    }

    @Override protected void write(PreparedStatement statement, UserInfo entity) throws SQLException {
        statement.setLong(1, entity.getId());
        statement.setString(2, entity.getInfo().toString());
    }

    @Override protected void writeWithoutId(PreparedStatement statement, UserInfo entity) throws SQLException {
        statement.setString(1, entity.getInfo().toString());
    }
}
