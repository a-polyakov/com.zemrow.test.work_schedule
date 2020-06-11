package com.zemrow.test.work_schedule.controller.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) графика работ на месяц
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class WorkScheduleDto {
    /**
     * Год.
     */
    private Integer year;
    /**
     * Месяц.
     */
    private Integer month;
    /**
     * Праздники.
     */
    private List<WorkScheduleDtoHoliday> holidays;
    /**
     * Пользователи.
     */
    private List<WorkScheduleDtoUser> users;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public List<WorkScheduleDtoHoliday> getHolidays() {
        return holidays;
    }

    public void setHolidays(List<WorkScheduleDtoHoliday> holidays) {
        this.holidays = holidays;
    }

    public List<WorkScheduleDtoUser> getUsers() {
        return users;
    }

    public void setUsers(List<WorkScheduleDtoUser> users) {
        this.users = users;
    }
}
