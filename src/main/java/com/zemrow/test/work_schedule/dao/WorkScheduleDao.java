package com.zemrow.test.work_schedule.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.zemrow.test.work_schedule.entity.WorkSchedule;

/**
 * DAO (data access object) для работы с графиком работ
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class WorkScheduleDao<T extends WorkSchedule> extends AbstractPeriodDao<T> {
    public static final String TABLE = "work_schedule";

    public static final String USER_ID = "user_id";

    @Override protected String getTableName() {
        return TABLE;
    }

    @Override protected String[] getColumns() {
        return new String[] {
            ID, USER_ID, START_TIME, END_TIME
        };
    }

    @Override protected T read(ResultSet resultSet) throws SQLException {
        final WorkSchedule result = new WorkSchedule();
        result.setId(resultSet.getLong(1));
        result.setUserId(resultSet.getLong(2));
        result.setStartTime(resultSet.getLong(3));
        result.setEndTime(resultSet.getLong(4));
        return (T)result;
    }

    @Override protected void write(PreparedStatement statement, WorkSchedule entity) throws SQLException {
        statement.setLong(1, entity.getId());
        statement.setLong(2, entity.getUserId());
        statement.setLong(3, entity.getStartTime());
        statement.setLong(4, entity.getEndTime());
    }

    @Override protected void writeWithoutId(PreparedStatement statement, WorkSchedule entity) throws SQLException {
        statement.setLong(1, entity.getUserId());
        statement.setLong(2, entity.getStartTime());
        statement.setLong(3, entity.getEndTime());
    }

    private String querySelectByUserAndPeriod;

    public List<T> selectByUserAndPeriod(Connection connection, long userId, long startTime, long endTime) throws SQLException {
        if (querySelectByUserAndPeriod == null) {
            synchronized (this) {
                if (querySelectByUserAndPeriod == null) {
                    final StringBuilder sb = new StringBuilder("select ");
                    sb.append(getAllColumns());
                    sb.append(" from ");
                    sb.append(getTableName());
                    sb.append(" where ");
                    sb.append(USER_ID);
                    sb.append("=? AND ");
                    sb.append(START_TIME);
                    sb.append("<=? AND ");
                    sb.append(END_TIME);
                    sb.append(">=?");
                    querySelectByUserAndPeriod = sb.toString();
                }
            }
        }
        try (final PreparedStatement statement = connection.prepareStatement(querySelectByUserAndPeriod)) {
            statement.setLong(1, userId);
            statement.setLong(2, endTime);
            statement.setLong(3, startTime);
            final ResultSet resultSet = statement.executeQuery();
            final List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(read(resultSet));
            }
            return result;
        }
    }
}
