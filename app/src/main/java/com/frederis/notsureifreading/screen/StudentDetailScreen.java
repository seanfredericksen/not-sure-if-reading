package com.frederis.notsureifreading.screen;

import android.net.Uri;
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
import com.frederis.notsureifreading.model.Logs;
import com.frederis.notsureifreading.model.RecentAssessment;
import com.frederis.notsureifreading.model.Student;
import com.frederis.notsureifreading.model.StudentPopupInfo;
import com.frederis.notsureifreading.model.Students;
import com.frederis.notsureifreading.util.RecentAssessmentsCreator;
import com.frederis.notsureifreading.view.StudentDetailView;

import java.util.ArrayList;

import javax.inject.Singleton;

import dagger.Provides;
import flow.Flow;
import flow.HasParent;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

@Layout(R.layout.student_detail_view)
@Transition({R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right})
public class StudentDetailScreen extends TransitionScreen implements HasParent<StudentsListScreen>, Blueprint {

    private final long mStudentId;

    public StudentDetailScreen(long studentId) {
        mStudentId = studentId;
    }

    @Override
    public String getMortarScopeName() {
        return "Student Detail Screen {" + "studentId = " + mStudentId + '}';
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @Override
    public StudentsListScreen getParent() {
        return new StudentsListScreen();
    }

    @dagger.Module(injects = StudentDetailView.class, addsTo = CoreBlueprint.Module.class)
    public class Module {

        @Provides
        Observable<Student> provideStudent(Students students) {
            return students.getStudent(mStudentId);
        }

        @Provides
        Presenter providePresenter(
                @MainScope Flow flow,
                Students students,
                Observable<Student> student,
                Observable<ArrayList<RecentAssessment>> assessments,
                Logs logs,
                ToolbarOwner actionBar) {
            return new Presenter(mStudentId, students, student, assessments, flow, logs, actionBar);
        }

        @Provides
        //TODO - Query better
        Observable<ArrayList<Assessment>> provideAssessments(Assessments assessments) {
            return assessments.getAll().map(new Func1<ArrayList<Assessment>, ArrayList<Assessment>>() {
                @Override
                public ArrayList<Assessment> call(ArrayList<Assessment> assessments) {
                    ArrayList<Assessment> filtered = new ArrayList<>();

                    for (Assessment assessment : assessments) {
                        if (assessment.getStudentId() == mStudentId) {
                            filtered.add(assessment);
                        }
                    }

                    return filtered;
                }
            });
        }

        @Provides
        Observable<ArrayList<RecentAssessment>> provideRecentAssessments(RecentAssessmentsCreator recentAssessmentsCreator,
                                                                         Observable<ArrayList<Assessment>> allAssessments) {
            return allAssessments.map(recentAssessmentsCreator);
        }
    }

    @Singleton
    public static class Presenter extends ViewPresenter<StudentDetailView> {

        private final long studentId;
        private final Students students;
        private final Observable<Student> student;
        private final Observable<ArrayList<RecentAssessment>> mAssessments;
        private final Flow flow;
        private final Logs logs;
        private final ToolbarOwner actionBar;

        private final Subject<String, String> name = BehaviorSubject.create();
        private final Subject<String, String> currentWords = BehaviorSubject.create();
        private final Subject<Uri, Uri> image = BehaviorSubject.create();

        public Presenter(long studentId,
                         Students students,
                         Observable<Student> student,
                         Observable<ArrayList<RecentAssessment>> assessments,
                         Flow flow,
                         Logs logs,
                         ToolbarOwner actionBar) {
            this.studentId = studentId;
            this.students = students;
            this.student = student;
            this.mAssessments = assessments;
            this.flow = flow;
            this.logs = logs;
            this.actionBar = actionBar;
        }

        private Observable<String> createNameObservable() {
            return student
                    .map(new Func1<Student, String>() {
                        @Override
                        public String call(Student student) {
                            return student.getName();
                        }
                    });
        }

        private Observable<String> createCurrentWordsObservable() {
            return student
                    .map(new Func1<Student, String>() {
                        @Override
                        public String call(Student student) {
                            return String.format("%d - %d",
                                    student.getStartingWord(),
                                    student.getEndingWord());
                        }
                    });
        }

        private Observable<Uri> createStudentImageObservable() {
            return student
                    .map(new Func1<Student, Uri>() {
                        @Override
                        public Uri call(Student student) {
                            return student.getImageUri();
                        }
                    });
        }


        @Override
        protected void onLoad(Bundle savedInstanceState) {
            final StudentDetailView view = getView();
            if (view == null) return;

            super.onLoad(savedInstanceState);

            ToolbarOwner.Config.Builder builder = new ToolbarOwner.Config.Builder()
                    .withShowHomeEnabled(true)
                    .withUpEnabled(true)
                    .withTitleResId(R.string.empty)
                    .withElevationDimensionResId(R.dimen.no_elevation);

            if (studentId != 0L) {
                builder.withActions(new ToolbarOwner.MenuActions(R.menu.student_detail, new ToolbarOwner.MenuActions.Callback() {
                    @Override
                    public void onConfigureOptionsMenu(Menu menu) {
                    }

                    @Override
                    public boolean onMenuItemSelected(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.edit_student) {
                            editStudent();
                            return true;
                        }

                        return false;
                    }
                }));
            }

            actionBar.setConfig(builder.build());

            createNameObservable().subscribe(name);
            createCurrentWordsObservable().subscribe(currentWords);
            createStudentImageObservable().subscribe(image);

            view.populateImage(image);
            view.populateName(name);
            view.populateCurrentWords(currentWords);
            view.populateCurrentAverage(mAssessments, student);

            view.showAssessments(mAssessments);
        }

        public void editStudent() {
            flow.goTo(new EditStudentScreen(studentId));
        }

        public void onAssessmentSelected(long id) {
            flow.goTo(new AssessmentScreen(id));
        }

        public void onAddAssessmentSelected() {
            flow.goTo(new PerformAssessmentScreen(studentId));
        }

    }

}
