package com.zemrow.test.work_schedule.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.zemrow.test.work_schedule.DataSourceWrapper;
import com.zemrow.test.work_schedule.dao.VacationDao;
import com.zemrow.test.work_schedule.entity.Vacation;
import com.zemrow.test.work_schedule.entity.VacationType;

/**
 * Контроллер: управление отпуском пользователя
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class VacationController {

    private final DataSourceWrapper dataSource;

    private final VacationDao vacationDao;

    public VacationController(DataSourceWrapper dataSource, VacationDao vacationDao) {
        this.dataSource = dataSource;
        this.vacationDao = vacationDao;
    }

    public Vacation insert(long userId, long startTime, long endTime, VacationType type) {
        try (final Connection connection = dataSource.getConnection()) {

            final List<Vacation> findVacation=vacationDao.selectByUserAndPeriodAndType(connection, userId, startTime, endTime, type);
            if (findVacation.isEmpty()) {
                final Vacation result = new Vacation(userId, startTime, endTime, type);
                vacationDao.insert(connection, result);
                connection.commit();
                return result;
            }
            else{
                // concat
                boolean needUpdate = false;
                for (Vacation vacation : findVacation) {
                    needUpdate=needUpdate || vacation.getStartTime() != startTime;
                    if (vacation.getStartTime() < startTime) {
                        startTime = vacation.getStartTime();
                    }
                    needUpdate=needUpdate || vacation.getEndTime() != endTime;
                    if (vacation.getEndTime() > endTime) {
                        endTime = vacation.getEndTime();
                    }
                }
                if (needUpdate) {
                    final Vacation vacation = findVacation.get(0);
                    vacation.setStartTime(startTime);
                    vacation.setEndTime(endTime);
                    vacation.setVacationType(type);
                    vacationDao.update(connection, vacation);
                }
                for (int i = 1; i < findVacation.size(); i++) {
                    vacationDao.delete(connection, findVacation.get(i).getId());
                }
                connection.commit();
                return findVacation.get(0);
            }
        }
        catch (SQLException e) {
            //TODO
            throw new RuntimeException(e);
        }
    }

    public void delete(long id) {
        try (final Connection connection = dataSource.getConnection()) {
            vacationDao.delete(connection, id);
            connection.commit();
        }
        catch (SQLException e) {
            //TODO
            throw new RuntimeException(e);
        }
    }
}
