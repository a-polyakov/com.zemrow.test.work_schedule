package com.zemrow.test.work_schedule.entity;

/**
 * Способы авторизации пользователя
 *
 * @author Alexandr Polyakov on 2018.04.13
 */
public class UserEntryPoint extends AbstractEntity {
    /**
     * ID пользователя
     */
    public Long userId;
    /**
     * Способ(система) авторизации: логин_пароль, vk.com, google, ...
     */
    public EntryPointTypeEnum entryPointType;
    /**
     * Идентификатор в системе авторизации (логин)
     */
    public String clientId;
    /**
     * Удостоверение личности в системе авторизации (пароль)
     */
    public String credential;

//================================ AUTO GENERATE ==============================

    public UserEntryPoint(Long id, Long userId, EntryPointTypeEnum entryPointType, String clientId,
        String credential) {
        super(id);
        this.userId = userId;
        this.entryPointType = entryPointType;
        this.clientId = clientId;
        this.credential = credential;
    }

    public UserEntryPoint(Long userId, EntryPointTypeEnum entryPointType, String clientId,
        String credential) {
        this(null, userId, entryPointType, clientId, credential);
    }

    public UserEntryPoint() {
        this(null, null, null, null, null);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public EntryPointTypeEnum getEntryPointType() {
        return entryPointType;
    }

    public void setEntryPointType(EntryPointTypeEnum entryPointType) {
        this.entryPointType = entryPointType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

}
