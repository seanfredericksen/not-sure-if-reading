package com.frederis.notsureifreading.model;

public class Assessment {

    private final String mTitle;
    private final long mId;

    Assessment(long id, String title) {
        mId = id;
        mTitle = title;
    }

    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public String toString() {
        return mTitle;
    }

}