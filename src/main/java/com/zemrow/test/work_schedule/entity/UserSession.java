package com.zemrow.test.work_schedule.entity;

/**
 * Сессия пользователя
 *
 * @author Alexandr Polyakov on 2018.08.18
 */
public class UserSession extends AbstractEntity {
    /**
     * Точка входа пользователя
     */
    public Long userEntryPointId;
    /**
     * Уникальный идентификатор сессии, сложный для подбора
     */
    public String token;
    /**
     * Дата создания записи
     */
    private Long createTime;

//================================ AUTO GENERATE ==============================

    public UserSession(Long id, Long userEntryPointId, String token, Long createTime) {
        super(id);
        this.userEntryPointId = userEntryPointId;
        this.token = token;
        this.createTime = createTime;
    }

    public UserSession(Long userEntryPointId, String token, Long createTime) {
        this(null, userEntryPointId, token, createTime);
    }

    public UserSession() {
        this(null, null, null, null);
    }

    public Long getUserEntryPointId() {
        return userEntryPointId;
    }

    public void setUserEntryPointId(Long userEntryPointId) {
        this.userEntryPointId = userEntryPointId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
