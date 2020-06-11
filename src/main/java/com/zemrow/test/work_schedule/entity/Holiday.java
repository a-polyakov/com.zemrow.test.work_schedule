package com.zemrow.test.work_schedule.entity;

/**
 * Праздник
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class Holiday extends AbstractPeriod {
    /**
     * Наименование праздника
     */
    private String title;
    /**
     * TRUE - календарный выходной является рабочим жнем.
     */
    private Boolean cancelWeekend;

//================================ AUTO GENERATE ==============================

    public Holiday(Long id, Long startTime, Long endTime, String title, Boolean cancelWeekend) {
        super(id, startTime, endTime);
        this.title = title;
        this.cancelWeekend = cancelWeekend;
    }
    public Holiday(Long startTime, Long endTime, String title, Boolean cancelWeekend) {
        this(null, startTime, endTime, title, cancelWeekend);
    }

    public Holiday(Long id, Long startTime, Long endTime, String title) {
        this(id, startTime, endTime, title, false);
    }

    public Holiday(Long startTime, Long endTime, String title) {
        this(null, startTime, endTime, title, false);
    }

    public Holiday() {
        this(null, null, null, null, false);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCancelWeekend() {
        return cancelWeekend;
    }

    public void setCancelWeekend(Boolean cancelWeekend) {
        this.cancelWeekend = cancelWeekend;
    }
}
