package com.zemrow.test.work_schedule;

import java.io.Closeable;
import java.io.IOException;
import com.zemrow.test.work_schedule.controller.DbVersionController;
import com.zemrow.test.work_schedule.controller.UserSessionController;
import com.zemrow.test.work_schedule.controller.VacationController;
import com.zemrow.test.work_schedule.controller.WorkScheduleController;
import com.zemrow.test.work_schedule.dao.DbVersionDao;
import com.zemrow.test.work_schedule.dao.HolidayDao;
import com.zemrow.test.work_schedule.dao.UserDao;
import com.zemrow.test.work_schedule.dao.UserEntryPointDao;
import com.zemrow.test.work_schedule.dao.UserInfoDao;
import com.zemrow.test.work_schedule.dao.UserRoleDao;
import com.zemrow.test.work_schedule.dao.UserSessionDao;
import com.zemrow.test.work_schedule.dao.VacationDao;
import com.zemrow.test.work_schedule.dao.WorkScheduleDao;

/**
 * Хранилище компонентов приложения
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class AppContext implements Closeable {

    private final DataSourceWrapper dataSource;

    private final DbVersionDao dbVersionDao;
    private final HolidayDao holidayDao;
    private final UserDao userDao;
    private final UserInfoDao userInfoDao;
    private final UserRoleDao userRoleDao;
    private final VacationDao vacationDao;
    private final WorkScheduleDao workScheduleDao;
    private final UserEntryPointDao userEntryPointDao;
    private final UserSessionDao userSessionDao;

    private final DbVersionController dbVersionController;
    private final WorkScheduleController workScheduleController;
    private final VacationController vacationController;
    private final UserSessionController userSessionController;

    private final WebServer webServer;

    public AppContext() throws ClassNotFoundException, IOException {
        dataSource = new DataSourceWrapper();

        dbVersionDao = new DbVersionDao();
        holidayDao = new HolidayDao();
        userDao = new UserDao();
        userInfoDao = new UserInfoDao();
        userRoleDao = new UserRoleDao();
        vacationDao = new VacationDao();
        workScheduleDao = new WorkScheduleDao();
        userEntryPointDao = new UserEntryPointDao();
        userSessionDao = new UserSessionDao();

        dbVersionController = new DbVersionController(dataSource, dbVersionDao, userDao, userInfoDao, userRoleDao, holidayDao, workScheduleDao, vacationDao, userEntryPointDao);
        workScheduleController = new WorkScheduleController(dataSource, holidayDao, userDao, workScheduleDao, vacationDao);
        vacationController = new VacationController(dataSource, vacationDao);
        userSessionController = new UserSessionController(dataSource, userEntryPointDao, userSessionDao);

        webServer = new WebServer(workScheduleController, vacationController, userSessionController);
    }

    @Override public void close() throws IOException {
        try {
            dataSource.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            webServer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DbVersionController getDbVersionController() {
        return dbVersionController;
    }

    public WebServer getWebServer() {
        return webServer;
    }
}
