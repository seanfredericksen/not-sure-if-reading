package com.frederis.notsureifreading.screen;

import android.os.Bundle;
import android.os.Handler;

import com.frederis.notsureifreading.MainBlueprint;
import com.frederis.notsureifreading.MainScope;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.actionbar.ToolbarOwner;
import com.frederis.notsureifreading.model.Assessment;
import com.frederis.notsureifreading.model.Assessments;
import com.frederis.notsureifreading.util.TitledBlueprint;
import com.frederis.notsureifreading.view.AssessmentView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import flow.Flow;
import flow.HasParent;
import flow.Layout;
import mortar.ViewPresenter;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;
import rx.subscriptions.Subscriptions;

@Layout(R.layout.assessment_view)
public class AssessmentScreen implements HasParent<RecentAssessmentListScreen>, TitledBlueprint {

    private final long mAssessmentId;

    public AssessmentScreen(long assessmentId) {
        mAssessmentId = assessmentId;
    }

    @Override public String getMortarScopeName() {
        return "AssessmentScreen {" + "assessmentId = " + mAssessmentId + '}';
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @Override public RecentAssessmentListScreen getParent() {
        return new RecentAssessmentListScreen();
    }

    @dagger.Module(injects = AssessmentView.class, addsTo = MainBlueprint.Module.class)
    public class Module {

        @Provides
        Observable<Assessment> provideAssessment(Assessments assessments) {
            return assessments.getAssessment(mAssessmentId);
        }

    }

    @Singleton
    public static class Presenter extends ViewPresenter<AssessmentView> {

        private final Assessments assessments;
        private final Observable<Assessment> assessment;
        private final Flow flow;
        private final ToolbarOwner actionBar;

        private final Subject<String, String> assessmentId = BehaviorSubject.create("Loading id..");
        private final Subject<String, String> assessmentName = BehaviorSubject.create("Loading name..");

        private Subscription running = Subscriptions.empty();

        @Inject
        public Presenter(Assessments assessments, Observable<Assessment> assessment, @MainScope Flow flow, ToolbarOwner actionBar) {
            this.assessments = assessments;
            this.assessment = assessment.delay(700, TimeUnit.MILLISECONDS);
            this.flow = flow;
            this.actionBar = actionBar;
        }

        @Override public void dropView(AssessmentView view) {
            super.dropView(view);
        }

        private Observable<String> createAssessmentIdObservable() {
            return assessment
                    .map(new Func1<Assessment, String>() {
                        @Override
                        public String call(Assessment assessment) {
                            return String.valueOf(assessment.getId());
                        }
                    });
        }

        private Observable<String> createAssessmentNameObservable() {
            return assessment
                    .map(new Func1<Assessment, String>() {
                        @Override
                        public String call(Assessment assessment) {
                            return assessment.getTitle();
                        }
                    });
        }

        @Override public void onLoad(Bundle savedInstanceState) {
            final AssessmentView v = getView();
            if (v == null) return;

            ToolbarOwner.Config actionBarConfig = actionBar.getConfig();

            actionBarConfig =
                    actionBarConfig.withAction(new ToolbarOwner.MenuAction("End", new Action0() {
                        @Override public void call() {
                        }
                    }));

            actionBar.setConfig(actionBarConfig);

            createAssessmentIdObservable().subscribe(assessmentId);
            createAssessmentNameObservable().subscribe(assessmentName);

            v.showAssessmentId(assessmentId);
            v.showAssessmentName(assessmentName);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    assessments.update();
                }
            }, 5000);
        }

        @Override protected void onExitScope() {
            ensureStopped();
        }

        public void visibilityChanged(boolean visible) {
            if (!visible) {
                ensureStopped();
            }
        }

        private void ensureStopped() {
            running.unsubscribe();
        }
    }

    @Override
    public CharSequence getTitle() {
        return "Assessment";
    }

}
