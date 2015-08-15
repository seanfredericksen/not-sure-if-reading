package com.frederis.notsureifreading.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.model.Assessment;
import com.frederis.notsureifreading.model.RecentAssessment;
import com.frederis.notsureifreading.model.Student;
import com.frederis.notsureifreading.model.StudentPopupInfo;
import com.frederis.notsureifreading.screen.StudentDetailScreen;
import com.frederis.notsureifreading.util.SubscriptionUtil;
import com.frederis.notsureifreading.widget.BezelImageView;

import java.util.ArrayList;

import javax.inject.Inject;

import mortar.Mortar;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

public class StudentDetailView extends FractionalLinearLayout {

    @Inject
    StudentDetailScreen.Presenter mPresenter;

    private final BezelImageView mStudentImage;
    private final TextView mName;
    private final TextView mCurrentWords;
    private final TextView mCurrentAverage;
    private RecentAssessmentsView mRecentAssessments;

    private final CompositeSubscription mCompositeSubscription;


    public StudentDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Mortar.inject(context, this);

        LayoutInflater.from(context).inflate(R.layout.student_detail_view_root, this, true);

        mRecentAssessments = (RecentAssessmentsView) findViewById(R.id.student_assessment_recycler);
        mStudentImage = (BezelImageView) findViewById(R.id.student_image);
        mName = (TextView) findViewById(R.id.student_name);
        mCurrentWords = (TextView) findViewById(R.id.current_words);
        mCurrentAverage = (TextView) findViewById(R.id.current_average);

        mRecentAssessments.setAddAssessmentButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onAddAssessmentSelected();
            }
        });

        mRecentAssessments.setOnAssessmentSelectedListener(new RecentAssessmentRecyclerView.OnAssessmentSelectedListener() {
            @Override
            public void onAssessmentSelected(long id) {
                mPresenter.onAssessmentSelected(id);
            }
        });

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

    public void showAssessments(Observable<ArrayList<RecentAssessment>> assessments) {
        mRecentAssessments.showAssessments(assessments);
    }

    public void populateCurrentAverage(Observable<ArrayList<RecentAssessment>> assessments, Observable<Student> student) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeTextViewText(assessments.zipWith(student, new Func2<ArrayList<RecentAssessment>, Student, ArrayList<RecentAssessment>>() {
            @Override
            public ArrayList<RecentAssessment> call(ArrayList<RecentAssessment> recentAssessments, Student student) {
                ArrayList<RecentAssessment> filtered = new ArrayList<>();

                for (RecentAssessment recentAssessment : recentAssessments) {
                    if (recentAssessment.assessment.getStartingWord() == student.getStartingWord() &&
                            recentAssessment.assessment.getEndingWord() == student.getEndingWord()) {
                        filtered.add(recentAssessment);

                    }
                }

                return filtered;
            }
        }).map(new Func1<ArrayList<RecentAssessment>, Integer>() {
            @Override
            public Integer call(ArrayList<RecentAssessment> recentAssessments) {
                float currentTotal = 0;

                for (RecentAssessment recentAssessment : recentAssessments) {
                    currentTotal += recentAssessment.percentAccuracy;
                }

                return (int) (currentTotal / recentAssessments.size());
            }
        }).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer average) {
                return getContext().getString(R.string.current_average, average, "%");
            }
        }), mCurrentAverage));
    }

    public void populateImage(Observable<Uri> imageUri) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeStudentImage(imageUri, getContext(), mStudentImage));
    }

    public void populateName(Observable<String> firstName) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeTextViewText(firstName, mName));
    }

    public void populateCurrentWords(Observable<String> currentWords) {
        mCompositeSubscription.add(SubscriptionUtil
                .subscribeTextViewText(currentWords.map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return getContext().getString(R.string.current_words, s);
                    }
                }), mCurrentWords));
    }

}
