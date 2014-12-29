package com.frederis.notsureifreading.model;

public class Student {

    private long mId;
    private String mName;
    private long mStartingWord;
    private long mEndingWord;

    public Student(long id, String name, long startingWord, long endingWord) {
        mId = id;
        mName = name;
        mStartingWord = startingWord;
        mEndingWord = endingWord;
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public long getStartingWord() {
        return mStartingWord;
    }

    public long getEndingWord() {
        return mEndingWord;
    }

    @Override
    public String toString() {
        return mName;
    }

}
