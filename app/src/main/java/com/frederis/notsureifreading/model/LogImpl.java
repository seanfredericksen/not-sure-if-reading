package com.frederis.notsureifreading.model;

import com.frederis.notsureifreading.database.Ideas.Logs;

import java.util.Date;

public class LogImpl implements Logs.Log {

    private final long mId;
    private final String mMessage;
    private final long mDate;

    public LogImpl(String message) {
        this(0L, message, new Date().getTime());
    }

    public LogImpl(long id, String message, long date) {
        mId = id;
        mMessage = message;
        mDate = date;
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public String getMessage() {
        return mMessage;
    }

    @Override
    public long getDate() {
        return mDate;
    }

}
