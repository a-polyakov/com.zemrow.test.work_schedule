package com.zemrow.test.work_schedule.controller.dto;

/**
 * Data Transfer Object (DTO) праздники
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class WorkScheduleDtoHoliday {
    private Long holidayId;
    private String title;
    private Long startTime;
    private Long endTime;

    public Long getHolidayId() {
        return holidayId;
    }

    public void setHolidayId(Long holidayId) {
        this.holidayId = holidayId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
