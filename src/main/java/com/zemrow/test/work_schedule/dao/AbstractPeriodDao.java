package com.zemrow.test.work_schedule.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.zemrow.test.work_schedule.entity.AbstractPeriod;

/**
 * DAO (data access object) для работы с периодом
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public abstract class AbstractPeriodDao<T extends AbstractPeriod> extends AbstractDao<T> {

    public static final String START_TIME="start_time";
    public static final String END_TIME="end_time";

    private String querySelectByPeriod;

    public List<T> selectByPeriod(Connection connection, long startTime, long endTime) throws SQLException {
        if (querySelectByPeriod == null) {
            synchronized (this) {
                if (querySelectByPeriod == null) {
                    final StringBuilder sb = new StringBuilder("select ");
                    sb.append(getAllColumns());
                    sb.append(" from ");
                    sb.append(getTableName());
                    sb.append(" where ");
                    sb.append(START_TIME);
                    sb.append("<=? AND ");
                    sb.append(END_TIME);
                    sb.append(">=?");
                    querySelectByPeriod = sb.toString();
                }
            }
        }
        try (final PreparedStatement statement = connection.prepareStatement(querySelectByPeriod)) {
            statement.setLong(1, endTime);
            statement.setLong(2, startTime);
            final ResultSet resultSet = statement.executeQuery();
            final List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(read(resultSet));
            }
            return result;
        }
    }
}
