package com.frederis.notsureifreading.screen;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import com.frederis.notsureifreading.CoreBlueprint;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.TransitionScreen;
import com.frederis.notsureifreading.actionbar.DrawerPresenter;
import com.frederis.notsureifreading.actionbar.ToolbarOwner;
import com.frederis.notsureifreading.animation.Transition;
import com.frederis.notsureifreading.model.Assessment;
import com.frederis.notsureifreading.model.Assessments;
import com.frederis.notsureifreading.view.RecentAssessmentListView;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;
import rx.Observable;
import rx.functions.Action0;

@Layout(R.layout.recent_assessment_list_view)
@Transition({R.animator.scale_fade_in, R.animator.scale_fade_out, R.animator.scale_fade_in, R.animator.scale_fade_out})
public class RecentAssessmentListScreen extends TransitionScreen implements Blueprint {

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = RecentAssessmentListView.class, addsTo = CoreBlueprint.Module.class)
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
        private final DrawerPresenter mDrawerPresenter;
        private final ToolbarOwner mActionBar;

        @Inject
        Presenter(Flow flow, Observable<ArrayList<Assessment>> assessments, DrawerPresenter drawerPresenter, ToolbarOwner actionBar) {
            mFlow = flow;
            mAssessments = assessments;
            mDrawerPresenter = drawerPresenter;
            mActionBar = actionBar;
        }

        @Override
        public void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            RecentAssessmentListView view = getView();
            if (view == null) return;

            mActionBar.setConfig(new ToolbarOwner.Config(true, true, "Assessments", null, R.dimen.toolbar_elevation));
            mDrawerPresenter.setConfig(new DrawerPresenter.Config(true, DrawerLayout.LOCK_MODE_UNLOCKED));

            view.showAssessments(mAssessments);
        }

        public void onAssessmentSelected(int position) {
            mFlow.goTo(new AssessmentScreen(position));
        }

    }

}