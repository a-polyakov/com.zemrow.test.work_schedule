package com.zemrow.test.work_schedule.controller.dto;

/**
 * Data Transfer Object (DTO) сессия пользователя
 *
 * @author Alexandr Polyakov on 2018.08.18
 */
public class SessionDto {
    /**
     * ID пользователя
     */
    private long userId;

//================================ AUTO GENERATE ==============================

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
