package com.frederis.notsureifreading.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.model.Assessment;
import com.frederis.notsureifreading.model.RecentAssessment;
import com.frederis.notsureifreading.model.Student;
import com.frederis.notsureifreading.model.StudentPopupInfo;
import com.frederis.notsureifreading.screen.RecentAssessmentListScreen;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import mortar.Mortar;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class RecentAssessmentListView extends FrameLayout {

    @Inject RecentAssessmentListScreen.Presenter presenter;

    private SelectStudentPopup mSelectStudentPopup;
    private RecentAssessmentRecyclerView mRecycler;
    private View mAddAssessment;

    private CompositeSubscription mCompositeSubscription;


    public RecentAssessmentListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize(context);
    }

    public RecentAssessmentListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RecentAssessmentListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initialize(context);

    }

    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.recent_assessment_list_view_root, this, true);

        mRecycler = (RecentAssessmentRecyclerView) findViewById(R.id.recent_assessment_recycler_view);
        mAddAssessment = findViewById(R.id.add_student_button);

        mRecycler.setOnAssessmentSelectedListener(new RecentAssessmentRecyclerView.OnAssessmentSelectedListener() {
            @Override
            public void onAssessmentSelected(long id) {
                presenter.onAssessmentSelected(id);
            }
        });

        Mortar.inject(context, this);

        mSelectStudentPopup = new SelectStudentPopup(context);

        mCompositeSubscription = new CompositeSubscription();
    }

    public SelectStudentPopup getSelectStudentPopup() {
        return mSelectStudentPopup;
    }


    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        presenter.takeView(this);
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mCompositeSubscription.clear();

        presenter.dropView(this);
    }

    public void showAssessments(Observable<ArrayList<RecentAssessment>> assessments) {
        mRecycler.showAssessments(assessments);
    }

    public void populateAddAssessmentButton(Observable<StudentPopupInfo> info) {
        mCompositeSubscription.add(info
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<StudentPopupInfo>() {
                    @Override
                    public void call(final StudentPopupInfo info) {
                        mAddAssessment.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                presenter.onAddAssessmentSelected(info);
                            }
                        });
                    }
                }));
    }

}
