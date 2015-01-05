package com.frederis.notsureifreading.screen;

import android.content.Context;
import android.os.Bundle;

import com.frederis.notsureifreading.CoreBlueprint;
import com.frederis.notsureifreading.ForApplication;
import com.frederis.notsureifreading.MainScope;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.actionbar.ToolbarOwner;
import com.frederis.notsureifreading.animation.Transition;
import com.frederis.notsureifreading.model.Assessment;
import com.frederis.notsureifreading.model.AssessmentAnswer;
import com.frederis.notsureifreading.model.Assessments;
import com.frederis.notsureifreading.model.RecentAssessment;
import com.frederis.notsureifreading.model.Word;
import com.frederis.notsureifreading.model.Words;
import com.frederis.notsureifreading.util.RecentAssessmentCreator;
import com.frederis.notsureifreading.view.AssessmentView;
import com.frederis.notsureifreading.view.RecentAssessmentListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import javax.inject.Singleton;

import dagger.Provides;
import flow.Flow;
import flow.HasParent;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;
import rx.subscriptions.Subscriptions;

@Layout(R.layout.assessment_view)
@Transition({R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right})
public class AssessmentScreen implements HasParent<RecentAssessmentListScreen>, Blueprint {

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

    @dagger.Module(injects = AssessmentView.class, addsTo = CoreBlueprint.Module.class)
    public class Module {

        @Provides
        Observable<Assessment> provideAssessment(Assessments assessments) {
            return assessments.getAssessment(mAssessmentId);
        }

        @Provides
        Presenter providePresenter(Observable<Assessment> assessment,
                                   RecentAssessmentCreator recentAssessmentsCreator,
                                   Words words,
                                   @ForApplication Context context,
                                   @MainScope Flow flow,
                                   ToolbarOwner toolbarOwner) {
            return new Presenter(mAssessmentId, context, assessment, recentAssessmentsCreator, words, flow, toolbarOwner);
        }

    }

    @Singleton
    public static class Presenter extends ViewPresenter<AssessmentView> {

        private final long mAssessmentId;
        private final Observable<Assessment> mAssessment;
        private final RecentAssessmentCreator mRecentAssessmentCreator;
        private final Words mWords;
        private final Flow mFlow;
        private final ToolbarOwner mToolbarOwner;
        private final Context mContext;
        private final SimpleDateFormat mDateFormat;

        private final Subject<String, String> mStudentName = BehaviorSubject.create();
        private final Subject<String, String> mAssessmentWords = BehaviorSubject.create();
        private final Subject<String, String> mAssessmentAccuracy = BehaviorSubject.create();
        private final Subject<String, String> mAssessmentDate = BehaviorSubject.create();
        private final Subject<ArrayList<AssessmentAnswer>, ArrayList<AssessmentAnswer>> mAssessmentAnswers = BehaviorSubject.create();

        private Subscription running = Subscriptions.empty();

        public Presenter(long assessmentId,
                         Context context,
                         Observable<Assessment> assessment,
                         RecentAssessmentCreator recentAssessmentCreator,
                         Words words,
                         Flow flow,
                         ToolbarOwner toolbarOwner) {
            mAssessmentId = assessmentId;
            mAssessment = assessment;
            mRecentAssessmentCreator = recentAssessmentCreator;
            mWords = words;
            mContext = context;
            mFlow = flow;
            mToolbarOwner = toolbarOwner;

            mDateFormat = new SimpleDateFormat("cccc, MMM d");
            mDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        @Override public void dropView(AssessmentView view) {
            super.dropView(view);
        }

        private Observable<String> createAssessmentWordsObservable() {
            return mAssessment
                    .map(new Func1<Assessment, String>() {
                        @Override
                        public String call(Assessment assessment) {
                            return mContext.getString(R.string.words_description, assessment.getStartingWord(), assessment.getEndingWord());
                        }
                    });
        }

        private Observable<String> createAssessmentDateObservable() {
            return mAssessment
                    .map(new Func1<Assessment, String>() {
                        @Override
                        public String call(Assessment assessment) {
                            return mDateFormat.format(assessment.getDate());
                        }
                    });
        }

        private Observable<String> createAssessmentAccuracyObservable() {
            return mAssessment.map(mRecentAssessmentCreator).map(new Func1<RecentAssessment, String>() {
                @Override
                public String call(RecentAssessment recentAssessment) {
                    return mContext.getString(R.string.assessment_accuracy, recentAssessment.percentAccuracy + "%");
                }
            });
        }

        private Observable<ArrayList<AssessmentAnswer>> createAssessmentAnswersObservable() {
            return mAssessment.map(new Func1<Assessment, ArrayList<AssessmentAnswer>>() {
                @Override
                public ArrayList<AssessmentAnswer> call(Assessment assessment) {
                    ArrayList<Word> words = mWords.getAssessmentWords(assessment);
                    ArrayList<AssessmentAnswer> answers = new ArrayList<>(words.size());

                    for (int i = 0; i < words.size(); i++) {
                        long result =
                                (i < 50
                                        ? assessment.getOneToFiftyResult()
                                        : assessment.getFiftyOneToOneHundredResult()
                                ) & (1L << (i < 50 ? (49 - i) : (99 - i)));

                        answers.add(new AssessmentAnswer(words.get(i), result != 0L));
                    }

                    return answers;
                }
            });
        }

        private Observable<String> createStudentNameObservable() {
            return mAssessment.map(mRecentAssessmentCreator).map(new Func1<RecentAssessment, String>() {
                @Override
                public String call(RecentAssessment recentAssessment) {
                    return recentAssessment.studentName;
                }
            });
        }

        @Override public void onLoad(Bundle savedInstanceState) {
            final AssessmentView v = getView();
            if (v == null) return;

            mToolbarOwner.setConfig(new ToolbarOwner.Config.Builder()
                    .withShowHomeEnabled(true)
                    .withUpEnabled(true)
                    .withTitleResId(R.string.assessment)
                    .withElevationDimensionResId(R.dimen.toolbar_elevation)
                    .build());

            createAssessmentWordsObservable().subscribe(mAssessmentWords);
            createAssessmentDateObservable().subscribe(mAssessmentDate);
            createAssessmentAccuracyObservable().subscribe(mAssessmentAccuracy);
            createAssessmentAnswersObservable().subscribe(mAssessmentAnswers);
            createStudentNameObservable().subscribe(mStudentName);

            v.showAssessmentWords(mAssessmentWords);
            v.showAssessmenDate(mAssessmentDate);
            v.showAssessmentAccuracy(mAssessmentAccuracy);
            v.showAssessmentAnswers(mAssessmentAnswers);
            v.showStudentName(mStudentName);
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

}
