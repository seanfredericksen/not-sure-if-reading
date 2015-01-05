package com.frederis.notsureifreading.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.model.AssessmentAnswer;
import com.frederis.notsureifreading.screen.AssessmentScreen;
import com.frederis.notsureifreading.util.SubscriptionUtil;

import java.util.ArrayList;

import javax.inject.Inject;

import mortar.Mortar;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class AssessmentView extends FrameLayout {

    @Inject AssessmentScreen.Presenter presenter;

    final private CompositeSubscription mCompositeSubscription;

    private TextView mAssessmentWords;
    private TextView mAssessmentDate;
    private TextView mAssessmentAccuracy;
    private TextView mStudentName;
    private AssessmentWordsRecyclerView mAssessmentAnswers;

    public AssessmentView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Mortar.inject(context, this);

        LayoutInflater.from(context).inflate(R.layout.assessment_view_root, this, true);

        mStudentName = (TextView) findViewById(R.id.assessment_student_name);
        mAssessmentWords = (TextView) findViewById(R.id.assessment_words);
        mAssessmentDate = (TextView) findViewById(R.id.assessment_date);
        mAssessmentAccuracy = (TextView) findViewById(R.id.assessment_accuracy);
        mAssessmentAnswers = (AssessmentWordsRecyclerView) findViewById(R.id.assessment_answers_recycler);

        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCompositeSubscription.clear();
        presenter.dropView(this);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        presenter.visibilityChanged(visibility == VISIBLE);
    }

    public void showAssessmentWords(Observable<String> assessmentWords) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeTextViewText(assessmentWords, mAssessmentWords));
    }

    public void showAssessmenDate(Observable<String> assessmentDate) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeTextViewText(assessmentDate, mAssessmentDate));
    }

    public void showAssessmentAccuracy(Observable<String> assessmentAccuracy) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeTextViewText(assessmentAccuracy, mAssessmentAccuracy));
    }

    public void showAssessmentAnswers(Observable<ArrayList<AssessmentAnswer>> assessmentAnswers) {
        mCompositeSubscription.add(mAssessmentAnswers.showAssessmentAnswers(assessmentAnswers));
    }

    public void showStudentName(Observable<String> studentName) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeTextViewText(studentName, mStudentName));
    }

}
