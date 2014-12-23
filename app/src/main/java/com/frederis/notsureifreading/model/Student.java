package com.frederis.notsureifreading.model;

public class Student {

    private long mId;
    private String mFirstName;
    private String mLastName;
    private long mStartingWord;
    private long mEndingWord;

    public Student(long id, String firstName, String lastName, long startingWord, long endingWord) {
        mId = id;
        mFirstName = firstName;
        mLastName = lastName;
        mStartingWord = startingWord;
        mEndingWord = endingWord;
    }

    public long getId() {
        return mId;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public long getStartingWord() {
        return mStartingWord;
    }

    public long getEndingWord() {
        return mEndingWord;
    }

    @Override
    public String toString() {
        return mFirstName + " " + mLastName;
    }
}
