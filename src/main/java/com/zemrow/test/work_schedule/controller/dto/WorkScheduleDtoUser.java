package com.zemrow.test.work_schedule.controller.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) графика работ пользователя
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class WorkScheduleDtoUser {
    private Long userId;
    private String fullName;
    private List<WorkScheduleDtoUserWork> works;
    private List<WorkScheduleDtoUserVacation> vacationList;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<WorkScheduleDtoUserWork> getWorks() {
        return works;
    }

    public void setWorks(List<WorkScheduleDtoUserWork> works) {
        this.works = works;
    }

    public List<WorkScheduleDtoUserVacation> getVacationList() {
        return vacationList;
    }

    public void setVacationList(
        List<WorkScheduleDtoUserVacation> vacationList) {
        this.vacationList = vacationList;
    }
}
