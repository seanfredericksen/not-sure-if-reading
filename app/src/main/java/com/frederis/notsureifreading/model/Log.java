package com.frederis.notsureifreading.model;

import java.util.Date;

public class Log {

    private final long mId;
    private final String mMessage;
    private final long mDate;

    public Log(String message) {
        this(0L, message, new Date().getTime());
    }

    public Log(long id, String message, long date) {
        mId = id;
        mMessage = message;
        mDate = date;
    }

    public long getId() {
        return mId;
    }

    public String getMessage() {
        return mMessage;
    }

    public long getDate() {
        return mDate;
    }

}
