package com.zemrow.test.work_schedule.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.zemrow.test.work_schedule.entity.Holiday;

/**
 * DAO (data access object) для работы со справочником праздников
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class HolidayDao extends AbstractPeriodDao<Holiday> {
    public static final String TABLE = "holiday";
    public static final String TITLE = "title";

    @Override protected String getTableName() {
        return TABLE;
    }

    @Override protected String[] getColumns() {
        return new String[] {
            ID, START_TIME, END_TIME, TITLE
        };
    }

    @Override protected Holiday read(ResultSet resultSet) throws SQLException {
        final Holiday result = new Holiday();
        result.setId(resultSet.getLong(1));
        result.setStartTime(resultSet.getLong(2));
        result.setEndTime(resultSet.getLong(3));
        result.setTitle(resultSet.getString(4));
        return result;
    }

    @Override protected void write(PreparedStatement statement, Holiday entity) throws SQLException {
        statement.setLong(1, entity.getId());
        statement.setLong(2, entity.getStartTime());
        statement.setLong(3, entity.getEndTime());
        statement.setString(4, entity.getTitle());
    }

    @Override protected void writeWithoutId(PreparedStatement statement, Holiday entity) throws SQLException {
        statement.setLong(1, entity.getStartTime());
        statement.setLong(2, entity.getEndTime());
        statement.setString(3, entity.getTitle());
    }
}
