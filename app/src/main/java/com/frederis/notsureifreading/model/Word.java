package com.frederis.notsureifreading.model;

public class Word {

    private long mId;
    private String mText;

    public Word(long id, String text) {
        mId = id;
        mText = text;
    }

    public long getId() {
        return mId;
    }

    public String getText() {
        return mText;
    }

}
