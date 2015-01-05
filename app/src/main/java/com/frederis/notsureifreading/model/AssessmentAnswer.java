package com.frederis.notsureifreading.model;

public class AssessmentAnswer {

    private Word mWord;
    private boolean mIsCorrect;

    public AssessmentAnswer(Word word) {
        this(word, false);
    }

    public AssessmentAnswer(Word word, boolean correct) {
        mWord = word;
        mIsCorrect = correct;
    }

    public Word getWord() {
        return mWord;
    }

    public boolean isCorrect() {
        return mIsCorrect;
    }

    public void setIsCorrect(boolean isCorrect) {
        mIsCorrect = isCorrect;
    }

}
