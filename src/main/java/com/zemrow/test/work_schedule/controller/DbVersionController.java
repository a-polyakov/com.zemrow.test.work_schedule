package com.zemrow.test.work_schedule.controller;

import com.zemrow.test.work_schedule.DataSourceWrapper;
import com.zemrow.test.work_schedule.dao.DbVersionDao;
import com.zemrow.test.work_schedule.dao.HolidayDao;
import com.zemrow.test.work_schedule.dao.UserDao;
import com.zemrow.test.work_schedule.dao.UserEntryPointDao;
import com.zemrow.test.work_schedule.dao.UserInfoDao;
import com.zemrow.test.work_schedule.dao.UserRoleDao;
import com.zemrow.test.work_schedule.dao.UserSessionDao;
import com.zemrow.test.work_schedule.dao.VacationDao;
import com.zemrow.test.work_schedule.dao.WorkScheduleDao;
import com.zemrow.test.work_schedule.entity.EntryPointTypeEnum;
import com.zemrow.test.work_schedule.entity.Holiday;
import com.zemrow.test.work_schedule.entity.User;
import com.zemrow.test.work_schedule.entity.UserEntryPoint;
import com.zemrow.test.work_schedule.entity.UserRole;
import com.zemrow.test.work_schedule.entity.UserRoleEnum;
import com.zemrow.test.work_schedule.entity.Vacation;
import com.zemrow.test.work_schedule.entity.VacationType;
import com.zemrow.test.work_schedule.entity.WorkSchedule;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Контроллер инициализации и обновления БД
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class DbVersionController {

    private final DataSourceWrapper dataSource;

    private final DbVersionDao dbVersionDao;
    private final UserDao userDao;
    private final UserInfoDao userInfoDao;
    private final UserRoleDao userRoleDao;
    private final HolidayDao holidayDao;
    private final WorkScheduleDao<WorkSchedule> workScheduleDao;
    private final VacationDao vacationDao;
    private final UserEntryPointDao userEntryPointDao;

    public DbVersionController(
        final DataSourceWrapper dataSource,
        final DbVersionDao dbVersionDao,
        final UserDao userDao,
        final UserInfoDao userInfoDao,
        final UserRoleDao userRoleDao,
        final HolidayDao holidayDao,
        final WorkScheduleDao<WorkSchedule> workScheduleDao,
        final VacationDao vacationDao,
        final UserEntryPointDao userEntryPointDao
    ) {
        this.dataSource = dataSource;
        this.dbVersionDao = dbVersionDao;
        this.userDao = userDao;
        this.userInfoDao = userInfoDao;
        this.userRoleDao = userRoleDao;
        this.holidayDao = holidayDao;
        this.workScheduleDao = workScheduleDao;
        this.vacationDao = vacationDao;
        this.userEntryPointDao = userEntryPointDao;
    }

    /**
     * Инициализация и обновления схемы БД.
     */
    public void update() {
        try (final Connection connection = dataSource.getConnection()) {

            final DatabaseMetaData metaData = connection.getMetaData();

            try (final ResultSet resultSet = metaData.getTables(null, null, DbVersionDao.TABLE, null)) {
                if (!resultSet.next()) {
                    applyV0(connection);
                }
                long version = dbVersionDao.getCurrentVersion(connection);
                if (version < 1) {
                    version = applyV1(connection);
                }
//                if (version < 2) {
//                    version = applyV2(connection);
//                }
            }
        }
        catch (Exception e) {
            //TODO
            throw new RuntimeException(e);
        }
    }

    private void applyV0(final Connection connection) throws SQLException {
        try (final Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE " + DbVersionDao.TABLE + "(" + DbVersionDao.ID + " bigint NOT NULL PRIMARY KEY" + ")");
            statement.execute("COMMENT ON TABLE " + DbVersionDao.TABLE + " IS 'Версия базы данных';");

            statement.execute("CREATE TABLE " + UserDao.TABLE + "(" +
                UserDao.ID + " bigint NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                UserDao.FULL_NAME + " varchar(255) NOT NULL UNIQUE" +
                ")");
            statement.execute("COMMENT ON TABLE " + UserDao.TABLE + " IS 'Пользователь';");

            statement.execute("CREATE TABLE " + UserInfoDao.TABLE + "(" +
                UserInfoDao.ID + " bigint NOT NULL PRIMARY KEY, " +
                UserInfoDao.INFO + " varchar(5000) NOT NULL," +
                "FOREIGN KEY (" + UserInfoDao.ID + ") REFERENCES " + UserDao.TABLE + "(" + UserDao.ID + ")" +
                ")");
            statement.execute("COMMENT ON TABLE " + UserInfoDao.TABLE + " IS 'Дополнительная информация о пользователе';");

            statement.execute("CREATE TABLE " + UserRoleDao.TABLE + "(" +
                UserRoleDao.ID + " bigint NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                UserRoleDao.USER_ID + " bigint NOT NULL, " +
                UserRoleDao.ROLE + " varchar(50) NOT NULL, " +
                "FOREIGN KEY (" + UserRoleDao.USER_ID + ") REFERENCES " + UserDao.TABLE + "(" + UserDao.ID + ")" +
                ")");
            statement.execute("COMMENT ON TABLE " + UserRoleDao.TABLE + " IS 'Права пользователя';");

            statement.execute("CREATE TABLE " + WorkScheduleDao.TABLE + "(" +
                WorkScheduleDao.ID + " bigint NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                WorkScheduleDao.USER_ID + " bigint NOT NULL, " +
                WorkScheduleDao.START_TIME + " bigint NOT NULL, " +
                WorkScheduleDao.END_TIME + " bigint NOT NULL, " +
                "FOREIGN KEY (" + WorkScheduleDao.USER_ID + ") REFERENCES " + UserDao.TABLE + "(" + UserDao.ID + ")" +
                ")");
            statement.execute("COMMENT ON TABLE " + WorkScheduleDao.TABLE + " IS 'Рабочий график пользователя';");

            statement.execute("CREATE TABLE " + VacationDao.TABLE + "(" +
                VacationDao.ID + " bigint NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                VacationDao.USER_ID + " bigint NOT NULL, " +
                VacationDao.START_TIME + " bigint NOT NULL, " +
                VacationDao.END_TIME + " bigint NOT NULL, " +
                VacationDao.VACATION_TYPE + " varchar(50) NOT NULL, " +
                "FOREIGN KEY (" + VacationDao.USER_ID + ") REFERENCES " + UserDao.TABLE + "(" + UserDao.ID + ")" +
                ")");
            statement.execute("COMMENT ON TABLE " + VacationDao.TABLE + " IS 'Отпуск/больничный';");

            statement.execute("CREATE TABLE " + HolidayDao.TABLE + "(" +
                HolidayDao.ID + " bigint NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                HolidayDao.START_TIME + " bigint NOT NULL, " +
                HolidayDao.END_TIME + " bigint NOT NULL, " +
                HolidayDao.TITLE + " varchar(255) NOT NULL" +
                ")");
            statement.execute("COMMENT ON TABLE " + HolidayDao.TABLE + " IS 'Праздник';");

            statement.execute("CREATE TABLE " + UserEntryPointDao.TABLE + "(" +
                UserEntryPointDao.ID + " bigint NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                UserEntryPointDao.USER_ID + " bigint NOT NULL, " +
                UserEntryPointDao.ENTRY_POINT_TYPE + " varchar(50) NOT NULL, " +
                UserEntryPointDao.CLIENT_ID + " varchar(100) NOT NULL, " +
                UserEntryPointDao.CREDENTIAL + " varchar(100), " +
                "FOREIGN KEY (" + UserEntryPointDao.USER_ID + ") REFERENCES " + UserDao.TABLE + "(" + UserDao.ID + ")" +
                ")");
            statement.execute("COMMENT ON TABLE " + UserEntryPointDao.TABLE + " IS 'Способы авторизации пользователя';");

            statement.execute("CREATE TABLE " + UserSessionDao.TABLE + "(" +
                UserSessionDao.ID + " bigint NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                UserSessionDao.USER_ENTRY_POINT_ID + " bigint NOT NULL, " +
                UserSessionDao.TOKEN + " varchar(73) NOT NULL, " +
                UserSessionDao.CREATE_TIME + " bigint NOT NULL, " +
                "FOREIGN KEY (" + UserSessionDao.USER_ENTRY_POINT_ID + ") REFERENCES " + UserEntryPointDao.TABLE + "(" + UserEntryPointDao.ID + ")" +
                ")");
            statement.execute("COMMENT ON TABLE " + UserSessionDao.TABLE + " IS 'Сессия пользователя';");

            connection.commit();
        }
    }

    private long applyV1(final Connection connection) throws SQLException, ParseException {
        User admin = insertUser(connection, "admin", "admin", UserRoleEnum.ADMIN_WORK_SCHEDULE);

        insertHoliday(connection, "Новогодние каникулы", "2018-01-01", "2018-01-08");
        insertHoliday(connection, "День защитника Отечества", "2018-02-23");
        insertHoliday(connection, "Международный женский день", "2018-03-08", "2018-03-09");
        insertHoliday(connection, "Перенос", "2018-04-28", true);
        insertHoliday(connection, "Праздник Весны и Труда", "2018-04-30", "2018-05-02");
        insertHoliday(connection, "День Победы", "2018-05-09");
        insertHoliday(connection, "Перенос", "2018-06-09", true);
        insertHoliday(connection, "День России", "2018-06-11", "2018-06-12");
        insertHoliday(connection, "День народного единства", "2018-11-05");
        insertHoliday(connection, "Перенос", "2018-12-29", true);
        insertHoliday(connection, "Новогодние каникулы", "2018-12-31", "2019-01-08");

        insertWorkScheduler(connection, admin,
            "2018-07-02 10:00", "2018-07-03 10:00",
            "2018-07-06 10:00", "2018-07-07 10:00",
            "2018-07-10 10:00", "2018-07-11 10:00",
            "2018-07-14 10:00", "2018-07-15 10:00",
            "2018-07-18 10:00", "2018-07-19 10:00",
            "2018-07-22 10:00", "2018-07-23 10:00",
            "2018-07-26 10:00", "2018-07-27 10:00",
            "2018-07-30 10:00", "2018-07-31 10:00"
        );

        insertVacation(connection, admin,
            "2018-07-09", "2018-07-15"
        );

        final long version = 1L;
        dbVersionDao.insert(connection, version);
        connection.commit();
        return version;
    }

    private User insertUser(final Connection connection, String fullName, String password,
        UserRoleEnum role) throws SQLException {
        final User user = new User(fullName);
        userDao.insert(connection, user);

        final UserEntryPoint userEntryPoint = new UserEntryPoint(user.getId(), EntryPointTypeEnum.LOGIN_PASSWORD, fullName, password);
        userEntryPointDao.insert(connection, userEntryPoint);

        final UserRole userRole = new UserRole(user.getId(), role);
        userRoleDao.insert(connection, userRole);

        // TODO
//        final JSONObject info = new JSONObject();
//        userInfoDao.insert(connection, new UserInfo(user.getId(), info));
        return user;
    }

    private void insertWorkScheduler(Connection connection, User user,
        String... time) throws ParseException, SQLException {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final int l = time.length;
        if (l % 2 != 0) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < l - 1; i += 2) {
            long startTime = format.parse(time[i]).getTime();
            long endTime = format.parse(time[i + 1]).getTime() + 60 * 1000 - 1;
            workScheduleDao.insert(connection, new WorkSchedule(user.getId(), startTime, endTime));
        }
    }

    private void insertVacation(Connection connection, User user,
        String... time) throws ParseException, SQLException {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final int l = time.length;
        if (l % 2 != 0) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < l - 1; i += 2) {
            long startTime = format.parse(time[i]).getTime();
            long endTime = format.parse(time[i + 1]).getTime() + 24 * 60 * 60 * 1000 - 1;
            vacationDao.insert(connection, new Vacation(user.getId(), startTime, endTime, VacationType.VACATION));
        }
    }

    private void insertHoliday(final Connection connection,
        String title, String startTime, String endTime, boolean cancelWeekend) throws SQLException {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            final long startTimeLong = format.parse(startTime).getTime();
            long endTimeLong;
            if (endTime != null) {
                endTimeLong = format.parse(endTime).getTime();
            }
            else {
                endTimeLong = startTimeLong;
            }
            endTimeLong += 24 * 60 * 60 * 1000 - 1;
            holidayDao.insert(connection, new Holiday(startTimeLong, endTimeLong, title, cancelWeekend));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void insertHoliday(final Connection connection,
        String title, String startTime, String endTime) throws SQLException {
        insertHoliday(connection, title, startTime, endTime, false);
    }

    private void insertHoliday(final Connection connection,
        String title, String startTime, Boolean cancelWeekend) throws SQLException {
        insertHoliday(connection, title, startTime, null, cancelWeekend);
    }

    private void insertHoliday(final Connection connection,
        String title, String startTime) throws SQLException {
        insertHoliday(connection, title, startTime, null, false);
    }

}
