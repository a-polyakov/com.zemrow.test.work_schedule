package com.zemrow.test.work_schedule.entity;

/**
 * Рабочий график пользователя
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class WorkSchedule extends AbstractPeriod {
    /**
     * Идентификатор пользователя
     */
    private Long userId;

//================================ AUTO GENERATE ==============================

    public WorkSchedule(Long id, Long userId, Long startTime, Long endTime) {
        super(id, startTime, endTime);
        this.userId = userId;
    }

    public WorkSchedule(Long userId, Long startTime, Long endTime) {
        this(null, userId, startTime, endTime);
    }

    public WorkSchedule() {
        this(null, null, null, null);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
