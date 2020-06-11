package com.zemrow.test.work_schedule.controller;

import com.zemrow.test.work_schedule.DataSourceWrapper;
import com.zemrow.test.work_schedule.controller.dto.SessionDto;
import com.zemrow.test.work_schedule.dao.UserEntryPointDao;
import com.zemrow.test.work_schedule.dao.UserSessionDao;
import com.zemrow.test.work_schedule.entity.EntryPointTypeEnum;
import com.zemrow.test.work_schedule.entity.UserEntryPoint;
import com.zemrow.test.work_schedule.entity.UserSession;
import java.sql.Connection;
import java.util.UUID;

/**
 * Контроллер пользовательской сессии
 *
 * @author Alexandr Polyakov on 2018.08.18
 */
public class UserSessionController {

    private final DataSourceWrapper dataSource;

    private final UserEntryPointDao userEntryPointDao;
    private final UserSessionDao userSessionDao;

    public UserSessionController(DataSourceWrapper dataSource, UserEntryPointDao userEntryPointDao,
        UserSessionDao userSessionDao) {
        this.dataSource = dataSource;
        this.userEntryPointDao = userEntryPointDao;
        this.userSessionDao = userSessionDao;
    }

    public String login(String username, String password) {
        try {
            try (final Connection connection = dataSource.getConnection()) {
                UserEntryPoint userEntryPoint = userEntryPointDao.selectByTypeAndClientId(connection, EntryPointTypeEnum.LOGIN_PASSWORD, username);
                //TODO
                // password.equals(userEntryPoint.getCredential());
                final UserSession userSession = new UserSession(userEntryPoint.getUserId(), UUID.randomUUID() + "-" + UUID.randomUUID(), System.currentTimeMillis());
                userSessionDao.insert(connection, userSession);
                connection.commit();
                return userSession.getToken();
            }
        }
        catch (Exception e) {
            //TODO
            throw new RuntimeException(e);
        }
    }

    public SessionDto check(String token) {
        try (final Connection connection = dataSource.getConnection()) {
            final SessionDto result = userSessionDao.selectByToken(connection, token);
            return result;
        }
        catch (Exception e) {
            //TODO
            throw new RuntimeException(e);
        }
    }
}
