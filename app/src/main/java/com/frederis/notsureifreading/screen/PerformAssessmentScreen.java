package com.frederis.notsureifreading.screen;

import android.os.Bundle;

import com.frederis.notsureifreading.MainBlueprint;
import com.frederis.notsureifreading.MainScope;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.model.Word;
import com.frederis.notsureifreading.model.Words;
import com.frederis.notsureifreading.util.TitledBlueprint;
import com.frederis.notsureifreading.view.PerformAssessmentView;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import flow.Flow;
import flow.Layout;
import mortar.ViewPresenter;
import rx.Observable;

@Layout(R.layout.perform_assessment_view)
public class PerformAssessmentScreen implements TitledBlueprint {

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

    @dagger.Module(injects = PerformAssessmentView.class, addsTo = MainBlueprint.Module.class)
    public class Module {

        @Provides
        Observable<ArrayList<Word>> provideWords(Words words) {
            return words.getCurrentWordsForStudent(mStudentId);
        }

        @Provides
        Presenter providePresenter(Observable<ArrayList<Word>> words, @MainScope Flow flow) {
            return new Presenter(words, flow);
        }

    }

    @Singleton
    public static class Presenter extends ViewPresenter<PerformAssessmentView> {

        private final Observable<ArrayList<Word>> mWords;
        private final Flow mFlow;

        @Inject
        Presenter(Observable<ArrayList<Word>> words, Flow flow) {
            mFlow = flow;
            mWords = words;
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            PerformAssessmentView view = getView();
            if (view == null) return;

            view.showWords(mWords);
        }

    }


    @Override
    public CharSequence getTitle() {
        return "Assessment";
    }
}
