package com.frederis.notsureifreading.model;

import android.os.Parcel;
import android.os.Parcelable;

public class WordsPopupInfo implements Parcelable {

    private long mMaxWord;

    public WordsPopupInfo(long maxWord) {
        mMaxWord = maxWord;
    }

    public long getMaxWord() {
        return mMaxWord;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mMaxWord);
    }

    public static Creator<WordsPopupInfo> CREATOR = new Creator<WordsPopupInfo>() {
        @Override
        public WordsPopupInfo createFromParcel(Parcel source) {
            return new WordsPopupInfo(source.readInt());
        }

        @Override
        public WordsPopupInfo[] newArray(int size) {
            return new WordsPopupInfo[size];
        }

    };

}
