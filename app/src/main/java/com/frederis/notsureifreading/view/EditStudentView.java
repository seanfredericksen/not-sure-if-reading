package com.frederis.notsureifreading.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.screen.EditStudentScreen;
import com.frederis.notsureifreading.util.SubscriptionUtil;

import javax.inject.Inject;

import mortar.Mortar;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class EditStudentView extends LinearLayout {

    @Inject EditStudentScreen.Presenter mPresenter;

    private final EditText mFirstName;
    private final EditText mLastName;
    private final EditText mStartinWord;
    private final EditText mEndingWord;

    private final CompositeSubscription mCompositeSubscription;

    public EditStudentView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Mortar.inject(context, this);

        LayoutInflater.from(context).inflate(R.layout.edit_student_view_root, this, true);

        mFirstName = (EditText) findViewById(R.id.first_name);
        mLastName = (EditText) findViewById(R.id.last_name);
        mStartinWord = (EditText) findViewById(R.id.starting_word);
        mEndingWord = (EditText) findViewById(R.id.ending_word);

        mCompositeSubscription = new CompositeSubscription();
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

    public void saveStudent() {
        mPresenter.updateOrInsertStudent(mFirstName.getText().toString(),
                mLastName.getText().toString(),
                Long.valueOf(mStartinWord.getText().toString()),
                Long.valueOf(mEndingWord.getText().toString()));
    }

    public void populateFirstName(Observable<String> firstName) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeTextViewText(firstName, mFirstName));
    }

    public void populateLastName(Observable<String> lastName) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeTextViewText(lastName, mLastName));
    }

    public void populateStartingWord(Observable<String> startingWord) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeTextViewText(startingWord, mStartinWord));
    }

    public void populateEndingWord(Observable<String> endingWord) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeTextViewText(endingWord, mEndingWord));
    }

}
