package com.frederis.notsureifreading.screen;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;

import com.frederis.notsureifreading.CoreBlueprint;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.TransitionScreen;
import com.frederis.notsureifreading.actionbar.DrawerPresenter;
import com.frederis.notsureifreading.actionbar.ToolbarOwner;
import com.frederis.notsureifreading.animation.Transition;
import com.frederis.notsureifreading.model.Assessment;
import com.frederis.notsureifreading.model.Assessments;
import com.frederis.notsureifreading.model.Students;
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
import rx.functions.Func1;

import static com.frederis.notsureifreading.view.RecentAssessmentListView.RecentAssessment;

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

        @Provides
        Observable<ArrayList<RecentAssessment>> provideRecentAssessments(final Students students, Observable<ArrayList<Assessment>> allAssessments) {
            return allAssessments.map(new RecentAssessmentCreator(students));
        }

    }

    @Singleton
    public static class Presenter extends ViewPresenter<RecentAssessmentListView> {

        private final Flow mFlow;
        private final Observable<ArrayList<RecentAssessment>> mAssessments;
        private final DrawerPresenter mDrawerPresenter;
        private final ToolbarOwner mActionBar;

        @Inject
        Presenter(Flow flow, Observable<ArrayList<RecentAssessment>> assessments, DrawerPresenter drawerPresenter, ToolbarOwner actionBar) {
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

    private static class RecentAssessmentCreator implements Func1<ArrayList<Assessment>, ArrayList<RecentAssessment>> {

        private Students students;

        public RecentAssessmentCreator(Students students) {
            this.students = students;
        }

        @Override
        public ArrayList<RecentAssessment> call(ArrayList<Assessment> assessments) {
            ArrayList<RecentAssessment> recentAssessments = new ArrayList<>();

            for (Assessment assessment : assessments) {
                recentAssessments.add(new RecentAssessment(assessment,
                        students.readStudent(assessment.getStudentId()).getName(),
                        calculateAccuracy(assessment)));
            }

            return recentAssessments;
        }

        private int calculateAccuracy(Assessment assessment) {
            float total = (float) (assessment.getEndingWord() - assessment.getStartingWord() + 1);
            float correct = 0.0f;

            for (int i = 0; i < total; i++) {
                long result =
                        (i < 50
                                ? assessment.getOneToFiftyResult()
                                : assessment.getFiftyOneToOneHundredResult()
                        ) & (1L << (i < 50 ? (49 - i) : (99 - i)));

                if (result != 0L) {
                    correct++;
                }
            }

            return Math.round((correct / total) * 100f);
        }
    }

}