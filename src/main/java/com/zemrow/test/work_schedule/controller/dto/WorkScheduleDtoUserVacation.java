package com.zemrow.test.work_schedule.controller.dto;

import com.zemrow.test.work_schedule.entity.VacationType;

/**
 * Data Transfer Object (DTO) отпуск пользователя
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class WorkScheduleDtoUserVacation {
    private Long vacationId;
    private VacationType type;
    private Long startTime;
    private Long endTime;

    public Long getVacationId() {
        return vacationId;
    }

    public void setVacationId(Long vacationId) {
        this.vacationId = vacationId;
    }

    public VacationType getType() {
        return type;
    }

    public void setType(VacationType type) {
        this.type = type;
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
