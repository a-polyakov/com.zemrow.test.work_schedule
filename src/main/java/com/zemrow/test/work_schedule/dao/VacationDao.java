package com.zemrow.test.work_schedule.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.zemrow.test.work_schedule.entity.Vacation;
import com.zemrow.test.work_schedule.entity.VacationType;

/**
 * DAO (data access object) для работы с отпуском пользователя
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class VacationDao extends WorkScheduleDao<Vacation> {
    public static final String TABLE = "vacation";

    public static final String VACATION_TYPE = "vacation_type";

    @Override protected String getTableName() {
        return TABLE;
    }

    @Override protected String[] getColumns() {
        return new String[] {
            ID, USER_ID, START_TIME, END_TIME, VACATION_TYPE
        };
    }

    @Override protected Vacation read(ResultSet resultSet) throws SQLException {
        final Vacation result = new Vacation();
        result.setId(resultSet.getLong(1));
        result.setUserId(resultSet.getLong(2));
        result.setStartTime(resultSet.getLong(3));
        result.setEndTime(resultSet.getLong(4));
        result.setVacationType(VacationType.valueOf(resultSet.getString(5)));
        return result;
    }

    @Override protected void write(PreparedStatement statement, Vacation entity) throws SQLException {
        statement.setLong(1, entity.getId());
        statement.setLong(2, entity.getUserId());
        statement.setLong(3, entity.getStartTime());
        statement.setLong(4, entity.getEndTime());
        statement.setString(5, entity.getVacationType().name());
    }

    @Override protected void writeWithoutId(PreparedStatement statement, Vacation entity) throws SQLException {
        statement.setLong(1, entity.getUserId());
        statement.setLong(2, entity.getStartTime());
        statement.setLong(3, entity.getEndTime());
        statement.setString(4, entity.getVacationType().name());
    }

    private String querySelectByUserAndPeriodAndType;

    public List<Vacation> selectByUserAndPeriodAndType(Connection connection, long userId, long startTime, long endTime, VacationType vacationType) throws SQLException {
        if (querySelectByUserAndPeriodAndType == null) {
            synchronized (this) {
                if (querySelectByUserAndPeriodAndType == null) {
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
                    sb.append(">=? AND ");
                    sb.append(VACATION_TYPE);
                    sb.append("=?");
                    querySelectByUserAndPeriodAndType = sb.toString();
                }
            }
        }
        try (final PreparedStatement statement = connection.prepareStatement(querySelectByUserAndPeriodAndType)) {
            statement.setLong(1, userId);
            statement.setLong(2, endTime);
            statement.setLong(3, startTime);
            statement.setString(4, vacationType.name());
            final ResultSet resultSet = statement.executeQuery();
            final List<Vacation> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(read(resultSet));
            }
            return result;
        }
    }
}
