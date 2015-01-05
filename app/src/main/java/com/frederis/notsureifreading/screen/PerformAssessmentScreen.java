package com.frederis.notsureifreading.screen;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.frederis.notsureifreading.CoreBlueprint;
import com.frederis.notsureifreading.MainScope;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.TransitionScreen;
import com.frederis.notsureifreading.actionbar.ToolbarOwner;
import com.frederis.notsureifreading.animation.Transition;
import com.frederis.notsureifreading.model.Assessment;
import com.frederis.notsureifreading.model.Assessments;
import com.frederis.notsureifreading.model.Word;
import com.frederis.notsureifreading.model.Words;
import com.frederis.notsureifreading.view.PerformAssessmentView;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;
import rx.Observable;
import rx.functions.Action0;

@Layout(R.layout.perform_assessment_view)
@Transition({R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right})
public class PerformAssessmentScreen extends TransitionScreen implements Blueprint {

    private final long mStudentId;

    public PerformAssessmentScreen(long studentId) {
        mStudentId = studentId;
    }

    @Override
    public String getMortarScopeName() {
        return "PerformAssessmentScreen { studentId = " + mStudentId + "}";
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = PerformAssessmentView.class, addsTo = CoreBlueprint.Module.class)
    public class Module {

        @Provides
        Observable<ArrayList<Word>> provideWords(Words words) {
            return words.getCurrentWordsForStudent(mStudentId);
        }

        @Provides
        Presenter providePresenter(Observable<ArrayList<Word>> words, Assessments assessments, @MainScope Flow flow, ToolbarOwner toolbarOwner) {
            return new Presenter(mStudentId, words, assessments, flow, toolbarOwner);
        }

    }

    @Singleton
    public static class Presenter extends ViewPresenter<PerformAssessmentView> {

        private final long mStudentId;
        private final Observable<ArrayList<Word>> mWords;
        private final Assessments mAssessments;
        private final Flow mFlow;
        private final ToolbarOwner mActionBar;

        @Inject
        Presenter(long studentId, Observable<ArrayList<Word>> words, Assessments assessments, Flow flow, ToolbarOwner actionBar) {
            mStudentId = studentId;
            mFlow = flow;
            mAssessments = assessments;
            mWords = words;
            mActionBar = actionBar;
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            final PerformAssessmentView view = getView();
            if (view == null) return;

            mActionBar.setConfig(new ToolbarOwner.Config.Builder()
                    .withShowHomeEnabled(true)
                    .withUpEnabled(true)
                    .withTitleResId(R.string.assessment)
                    .withElevationDimensionResId(R.dimen.no_elevation)
                    .withActions(new ToolbarOwner.MenuActions(R.menu.perform_assessment, new ToolbarOwner.MenuActions.Callback() {
                        @Override
                        public void onConfigureOptionsMenu(Menu menu) {
                        }

                        @Override
                        public boolean onMenuItemSelected(MenuItem menuItem) {
                            if (menuItem.getItemId() == R.id.save_assessment) {
                                mAssessments.updateOrInsertAssessment(getAssessment());
                                mFlow.goBack();
                                return true;
                            }

                            return false;
                        }
                    }))
                    .build());

            view.showWords(mWords);
        }

        private Assessment getAssessment() {
            PerformAssessmentView view = getView();

            return new Assessment(0L,
                    mStudentId,
                    new Date().getTime(),
                    view.getStartingWord(),
                    view.getEndingWord(),
                    view.get1To50Results(),
                    view.get51To100Results());
        }

    }

}
