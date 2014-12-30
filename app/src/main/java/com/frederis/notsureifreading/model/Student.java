package com.frederis.notsureifreading.model;

import android.net.Uri;

public class Student {

    public static final Uri IMAGE_UNCHANGED = Uri.parse("UNCHANGED");

    private long mId;
    private String mName;
    private Uri mImageUri;
    private long mStartingWord;
    private long mEndingWord;

    public Student(long id, String name, Uri imageUri, long startingWord, long endingWord) {
        mId = id;
        mName = name;
        mImageUri = imageUri;
        mStartingWord = startingWord;
        mEndingWord = endingWord;
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Uri getImageUri() {
        return mImageUri;
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
