package com.zemrow.test.work_schedule.entity;

/**
 * Объект хранящийся в БД
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public abstract class AbstractEntity {
    /**
     * Идентификатор
     */
    private Long id;

//================================ AUTO GENERATE ==============================

    public AbstractEntity(Long id) {
        this.id = id;
    }

    public AbstractEntity() {
        this(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
