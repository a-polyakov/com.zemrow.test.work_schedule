package com.zemrow.test.work_schedule.entity;

/**
 * Права пользователя
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class UserRole extends AbstractEntity {
    /**
     * Идентификатор пользователя
     */
    private Long userId;
    /**
     * Наименование роли
     */
    private UserRoleEnum role;

//================================ AUTO GENERATE ==============================

    public UserRole(Long id, Long userId, UserRoleEnum role) {
        super(id);
        this.userId = userId;
        this.role = role;
    }

    public UserRole(Long userId, UserRoleEnum role) {
        this(null, userId, role);
    }

    public UserRole() {
        this(null, null, null);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserRoleEnum getRole() {
        return role;
    }

    public void setRole(UserRoleEnum role) {
        this.role = role;
    }
}
