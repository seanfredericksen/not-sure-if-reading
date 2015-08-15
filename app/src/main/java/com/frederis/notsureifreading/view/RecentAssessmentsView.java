package com.frederis.notsureifreading.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.model.RecentAssessment;
import com.frederis.notsureifreading.util.SubscriptionUtil;

import java.util.ArrayList;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class RecentAssessmentsView extends FrameLayout implements SubscriptionUtil.ListDataHandler<ArrayList<RecentAssessment>> {

    private RecentAssessmentRecyclerView mRecycler;
    private View mEmpty;
    private View mAddAssessment;

    private RecentAssessmentRecyclerView.OnAssessmentSelectedListener mListener;

    private CompositeSubscription mCompositeSubscription;


    public RecentAssessmentsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize(context);
    }

    public RecentAssessmentsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RecentAssessmentsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initialize(context);

    }

    public void setOnAssessmentSelectedListener(RecentAssessmentRecyclerView.OnAssessmentSelectedListener listener) {
        mListener = listener;
    }

    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.recent_assessment_list_view_root, this, true);

        mRecycler = (RecentAssessmentRecyclerView) findViewById(R.id.recent_assessment_recycler_view);
        mEmpty = findViewById(R.id.recent_assessment_empty_view);
        mAddAssessment = findViewById(R.id.add_student_button);

        mRecycler.setOnAssessmentSelectedListener(new RecentAssessmentRecyclerView.OnAssessmentSelectedListener() {
            @Override
            public void onAssessmentSelected(long id) {
                if (mListener != null) {
                    mListener.onAssessmentSelected(id);
                }
            }
        });

        mCompositeSubscription = new CompositeSubscription();
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mCompositeSubscription.clear();
    }

    public void showAssessments(Observable<ArrayList<RecentAssessment>> assessments) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeListView(assessments, this));
    }

    public void setAddAssessmentButtonClickListener(OnClickListener onClickListener) {
        mAddAssessment.setOnClickListener(onClickListener);
    }

    @Override
    public void setListData(ArrayList<RecentAssessment> data) {
        if (data.size() > 0) {
            mRecycler.setListData(data);
            mEmpty.setVisibility(GONE);
            mRecycler.setVisibility(VISIBLE);
        } else {
            mRecycler.setVisibility(GONE);
            mEmpty.setVisibility(VISIBLE);
        }
    }

}
