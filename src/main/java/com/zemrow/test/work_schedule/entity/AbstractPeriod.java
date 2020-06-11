package com.zemrow.test.work_schedule.entity;

/**
 * Период
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public abstract class AbstractPeriod extends AbstractEntity {
    /**
     * Дата начала
     */
    private Long startTime;
    /**
     * Дата завершения
     */
    private Long endTime;

//================================ AUTO GENERATE ==============================

    public AbstractPeriod(Long id, Long startTime, Long endTime) {
        super(id);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public AbstractPeriod(Long startTime, Long endTime) {
        this(null, startTime, endTime);
    }

    public AbstractPeriod() {
        this(null, null, null);
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
