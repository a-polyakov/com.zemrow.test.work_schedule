package com.zemrow.test.work_schedule.entity;

/**
 * Пользователь
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class User extends AbstractEntity {
    /**
     * Полное наименование
     */
    private String fullName;

//================================ AUTO GENERATE ==============================

    public User(Long id, String fullName) {
        super(id);
        this.fullName = fullName;
    }

    public User(String fullName) {
        this(null, fullName);
    }

    public User() {
        this(null, null);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
