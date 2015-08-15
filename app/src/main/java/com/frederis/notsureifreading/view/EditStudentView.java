package com.frederis.notsureifreading.view;

import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.model.WordsPopupInfo;
import com.frederis.notsureifreading.screen.EditStudentScreen;
import com.frederis.notsureifreading.util.SubscriptionUtil;
import com.frederis.notsureifreading.widget.BezelImageView;

import javax.inject.Inject;

import mortar.Mortar;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class EditStudentView extends FractionalLinearLayout {

    @Inject EditStudentScreen.Presenter mPresenter;

    private final BezelImageView mStudentImage;
    private final EditText mName;
    private final EditText mStartingWord;
    private final EditText mEndingWord;
    private final Button mCancel;
    private final Button mSave;

    private InvalidWordsPopup mInvalidWordsPopup;

    private final CompositeSubscription mCompositeSubscription;
    private long mMaxWords;

    public EditStudentView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Mortar.inject(context, this);

        LayoutInflater.from(context).inflate(R.layout.edit_student_view_root, this, true);

        mStudentImage = (BezelImageView) findViewById(R.id.edit_student_image);
        mName = (EditText) findViewById(R.id.edit_name);
        mStartingWord = (EditText) findViewById(R.id.starting_word);
        mEndingWord = (EditText) findViewById(R.id.ending_word);
        mCancel = (Button) findViewById(R.id.cancel_button);
        mSave = (Button) findViewById(R.id.save_button);

        TextWatcher updatedTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateSaveEnabled();
            }
        };

        mName.addTextChangedListener(updatedTextWatcher);
        mStartingWord.addTextChangedListener(updatedTextWatcher);
        mEndingWord.addTextChangedListener(updatedTextWatcher);

        mStudentImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.captureImage();
            }
        });

        mCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.exit();
            }
        });

        mSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long startingWord = getStartingWord();
                long endingWord = getEndingWord();

                if (startingWord > 0 && startingWord <= mMaxWords
                        && endingWord > 0 && endingWord <= mMaxWords) {
                    saveStudent(startingWord, endingWord);
                } else {
                    mPresenter.onInvalidWordsEntered(new WordsPopupInfo(mMaxWords));
                }

            }
        });

        mCompositeSubscription = new CompositeSubscription();
        mInvalidWordsPopup = new InvalidWordsPopup(context);
    }

    private void updateSaveEnabled() {
        String name = mName.getText().toString();
        String startingWord = mStartingWord.getText().toString();
        String endingWord = mEndingWord.getText().toString();

        mSave.setEnabled(!name.isEmpty() && !startingWord.isEmpty() && !endingWord.isEmpty());
    }

    public InvalidWordsPopup getInvalidWordsPopup() {
        return mInvalidWordsPopup;
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();

        mPresenter.takeView(this);
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mCompositeSubscription.clear();

        mPresenter.dropView(this);
    }

    public void saveStudent(long startingWord, long endingWord) {
        mPresenter.updateOrInsertStudent(mName.getText().toString(),
                startingWord,
                endingWord);
        mPresenter.exit();
    }

    private long getStartingWord() {
        return Long.valueOf(mStartingWord.getText().toString());
    }

    private long getEndingWord() {
        return Long.valueOf(mEndingWord.getText().toString());
    }

    public void populateImage(Observable<Uri> imageUri) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeStudentImage(imageUri, getContext(), mStudentImage));
    }

    public void populateWordInfo(long maxWords) {
        mMaxWords = maxWords;
    }

    public void populateName(Observable<String> firstName) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeTextViewText(firstName, mName));
    }

    public void populateStartingWord(Observable<String> startingWord) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeTextViewText(startingWord, mStartingWord));
    }

    public void populateEndingWord(Observable<String> endingWord) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeTextViewText(endingWord, mEndingWord));
    }

}
