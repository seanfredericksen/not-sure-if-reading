package com.frederis.notsureifreading.screen;

import android.os.Bundle;

import com.frederis.notsureifreading.MainBlueprint;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.model.Assessment;
import com.frederis.notsureifreading.model.Assessments;
import com.frederis.notsureifreading.util.TitledBlueprint;
import com.frederis.notsureifreading.view.RecentAssessmentListView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;
import rx.Observable;

@Layout(R.layout.recent_assessment_list_view) //
public class RecentAssessmentListScreen implements TitledBlueprint {

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = RecentAssessmentListView.class, addsTo = MainBlueprint.Module.class)
    static class Module {

        @Provides
        Observable<ArrayList<Assessment>> provideAssessments(Assessments assessments) {
            return assessments.getAll();
        }

    }

    @Singleton
    public static class Presenter extends ViewPresenter<RecentAssessmentListView> {

        private final Flow mFlow;
        private final Observable<ArrayList<Assessment>> mAssessments;

        @Inject
        Presenter(Flow flow, Observable<ArrayList<Assessment>> assessments) {
            mFlow = flow;
            mAssessments = assessments;
        }

        @Override
        public void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            RecentAssessmentListView view = getView();
            if (view == null) return;

            view.showAssessments(mAssessments);
        }

        public void onAssessmentSelected(int position) {
            mFlow.goTo(new AssessmentScreen(position));
        }

    }

    @Override
    public CharSequence getTitle() {
        return "Recent Assessments";
    }

}