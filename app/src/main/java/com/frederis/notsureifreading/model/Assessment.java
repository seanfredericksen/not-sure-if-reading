package com.frederis.notsureifreading.model;

import com.frederis.notsureifreading.database.DatabaseDao;

public class Assessment implements DatabaseDao.DatabaseObject {

    private final long mId;
    private final long mStudentId;
    private final long mDate;
    private final long mStartingWord;
    private final long mEndingWord;
    private final long mOneToFiftyResult;
    private final long mFiftyOneToOneHundredResult;

    public Assessment(long id,
               long studentId,
               long date,
               long startingWord,
               long endingWord,
               long oneToFiftyResult,
               long fiftyOneToOneHundredResult) {
        mId = id;
        mStudentId = studentId;
        mDate = date;
        mStartingWord = startingWord;
        mEndingWord = endingWord;
        mOneToFiftyResult = oneToFiftyResult;
        mFiftyOneToOneHundredResult = fiftyOneToOneHundredResult;
    }

    @Override
    public long getId() {
        return mId;
    }

    public long getStudentId() {
        return mStudentId;
    }

    public long getDate() {
        return mDate;
    }

    public long getStartingWord() {
        return mStartingWord;
    }

    public long getEndingWord() {
        return mEndingWord;
    }

    public long getOneToFiftyResult() {
        return mOneToFiftyResult;
    }

    public long getFiftyOneToOneHundredResult() {
        return mFiftyOneToOneHundredResult;
    }
}