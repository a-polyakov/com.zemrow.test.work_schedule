package com.zemrow.test.work_schedule.entity;

import org.json.JSONObject;

/**
 * Дополнительная информация о пользователе
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class UserInfo extends AbstractEntity {
    private JSONObject info;

//================================ AUTO GENERATE ==============================

    public UserInfo(Long id, JSONObject info) {
        super(id);
        this.info = info;
    }

    public UserInfo() {
        this(null, null);
    }

    public JSONObject getInfo() {
        return info;
    }

    public void setInfo(JSONObject info) {
        this.info = info;
    }
}
