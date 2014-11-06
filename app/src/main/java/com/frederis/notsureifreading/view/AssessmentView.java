package com.frederis.notsureifreading.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.screen.AssessmentScreen;
import com.frederis.notsureifreading.util.SubscriptionUtil;

import javax.inject.Inject;

import mortar.Mortar;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class AssessmentView extends FrameLayout {

    @Inject AssessmentScreen.Presenter presenter;

    final private CompositeSubscription mCompositeSubscription;

    private TextView mAssessmentName;
    private TextView mAssessmentId;


    public AssessmentView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Mortar.inject(context, this);

        LayoutInflater.from(context).inflate(R.layout.assessment_view_root, this, true);
        mAssessmentName = (TextView) findViewById(R.id.assessment_name);
        mAssessmentId = (TextView) findViewById(R.id.assessment_id);

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

    public void showAssessmentName(Observable<String> assessmentName) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeTextViewText(assessmentName, mAssessmentName));

    }

    public void showAssessmentId(Observable<String> assessmentId) {
        mCompositeSubscription.add(SubscriptionUtil.subscribeTextViewText(assessmentId, mAssessmentId));

    }

}
