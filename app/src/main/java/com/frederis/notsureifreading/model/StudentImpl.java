package com.frederis.notsureifreading.model;

import android.net.Uri;

import com.frederis.notsureifreading.database.Ideas.Students;

public class StudentImpl implements Students.Student {

    private long mId;
    private String mName;
    private Uri mImageUri;
    private long mStartingWord;
    private long mEndingWord;

    public StudentImpl(long id, String name, String imageUriString, long startingWord, long endingWord) {
        mId = id;
        mName = name;
        mImageUri = Uri.parse(imageUriString);
        mStartingWord = startingWord;
        mEndingWord = endingWord;
    }

    public StudentImpl(long id, String name, Uri imageUri, long startingWord, long endingWord) {
        mId = id;
        mName = name;
        mImageUri = imageUri;
        mStartingWord = startingWord;
        mEndingWord = endingWord;
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getImageUri() {
        return mImageUri.toString();
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
