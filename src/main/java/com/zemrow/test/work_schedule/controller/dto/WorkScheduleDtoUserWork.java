package com.zemrow.test.work_schedule.controller.dto;

/**
 * Data Transfer Object (DTO) график работы пользователя
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class WorkScheduleDtoUserWork {
    private Long workScheduleId;
    private Long startTime;
    private Long endTime;

    public Long getWorkScheduleId() {
        return workScheduleId;
    }

    public void setWorkScheduleId(Long workScheduleId) {
        this.workScheduleId = workScheduleId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
