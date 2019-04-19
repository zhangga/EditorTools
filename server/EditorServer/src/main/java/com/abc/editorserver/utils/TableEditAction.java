package com.abc.editorserver.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TableEditAction
 * Created by Marco
 * Date: 2019/4/18 21:31
 */

public class TableEditAction {
    public String userId;
    public String dateTime;

    public TableEditAction(String userId) {
        this.userId = userId;
        setDateTime();
    }

    public TableEditAction(String userId, String dateTime) {
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public String getUserId() {
        return userId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setDateTime() {
        long currTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(currTime);
        this.dateTime = format.format(date);
    }

    @Override
    public String toString() {
        return "最近一次编辑由" + this.userId + "在" + dateTime + "进行";
    }
}
