package com.zemrow.test.work_schedule.entity;

/**
 * Отпуск/больничный
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class Vacation extends WorkSchedule {
    /**
     * Тип
     */
    private VacationType vacationType;

//================================ AUTO GENERATE ==============================

    public Vacation(Long id, Long userId, Long startTime, Long endTime, VacationType vacationType) {
        super(id, userId, startTime, endTime);
        this.vacationType = vacationType;
    }
    public Vacation(Long userId, Long startTime, Long endTime, VacationType vacationType) {
        this(null, userId, startTime, endTime, vacationType);
    }

    public Vacation() {
        this(null, null, null, null, null);
    }

    public VacationType getVacationType() {
        return vacationType;
    }

    public void setVacationType(VacationType vacationType) {
        this.vacationType = vacationType;
    }
}
