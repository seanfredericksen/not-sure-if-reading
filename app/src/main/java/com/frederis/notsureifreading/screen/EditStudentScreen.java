package com.frederis.notsureifreading.screen;

import android.net.Uri;
import android.os.Bundle;

import com.frederis.notsureifreading.MainBlueprint;
import com.frederis.notsureifreading.MainScope;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.actionbar.ToolbarOwner;
import com.frederis.notsureifreading.model.Student;
import com.frederis.notsureifreading.model.Students;
import com.frederis.notsureifreading.util.TitledBlueprint;
import com.frederis.notsureifreading.view.EditStudentView;

import javax.inject.Singleton;

import dagger.Provides;
import flow.Flow;
import flow.HasParent;
import flow.Layout;
import mortar.ViewPresenter;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

@Layout(R.layout.edit_student_view)
public class EditStudentScreen implements HasParent<StudentsListScreen>, TitledBlueprint {

    private final long mStudentId;

    public EditStudentScreen(long studentId) {
        mStudentId = studentId;
    }

    @Override
    public String getMortarScopeName() {
        return "Edit Student Screen {" + "studentId = " + mStudentId + '}';
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @Override
    public StudentsListScreen getParent() {
        return new StudentsListScreen();
    }

    @dagger.Module(injects = EditStudentView.class, addsTo = MainBlueprint.Module.class)
    public class Module {

        @Provides
        Observable<Student> provideStudent(Students students) {
            return students.getStudent(mStudentId);
        }

        @Provides
        Presenter providePresenter(Students students,
                                   Observable<Student> student,
                                   @MainScope Flow flow,
                                   ToolbarOwner actionBar) {
            return new Presenter(mStudentId, students, student, flow, actionBar);
        }

    }

    @Singleton
    public static class Presenter extends ViewPresenter<EditStudentView> {

        private final long studentId;
        private final Students students;
        private final Observable<Student> student;
        private final Flow flow;
        private final ToolbarOwner actionBar;

        private final Subject<String, String> name = BehaviorSubject.create();
        private final Subject<String, String> startingWord = BehaviorSubject.create();
        private final Subject<String, String> endingWord = BehaviorSubject.create();

        public Presenter(long studentId,
                         Students students,
                         Observable<Student> student,
                         Flow flow,
                         ToolbarOwner actionBar) {
            this.studentId = studentId;
            this.students = students;
            this.student = student;
            this.flow = flow;
            this.actionBar = actionBar;
        }

        private Observable<Long> createStudentIdObservable() {
            return student.map(new Func1<Student, Long>() {
                @Override
                public Long call(Student student) {
                    return student.getId();
                }
            });
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

        private Observable<String> createStartingWordObservable() {
            return student
                    .map(new Func1<Student, String>() {
                        @Override
                        public String call(Student student) {
                            return String.valueOf(student.getStartingWord());
                        }
                    });
        }

        private Observable<String> createEndingWordObservable() {
            return student
                    .map(new Func1<Student, String>() {
                        @Override
                        public String call(Student student) {
                            return String.valueOf(student.getEndingWord());
                        }
                    });
        }

        public void updateOrInsertStudent(String name,
                                          long startingWord,
                                          long endingWord) {
            students.updateOrInsertStudent(new Student(studentId, name, startingWord, endingWord));
        }

        public void assessStudent() {
            flow.goTo(new PerformAssessmentScreen(studentId));
        }

        @Override public void dropView(EditStudentView view) {
            super.dropView(view);
        }

        @Override public void onLoad(Bundle savedInstanceState) {
            final EditStudentView v = getView();
            if (v == null) return;

            ToolbarOwner.Config actionBarConfig = actionBar.getConfig();

            actionBarConfig =
                    actionBarConfig.withAction(new ToolbarOwner.MenuAction("Save", new Action0() {
                        @Override
                        public void call() {
                            v.saveStudent();
                            flow.goBack();
                        }
                    })).withElevationDimension(R.dimen.no_elevation);

            actionBar.setConfig(actionBarConfig);

            createNameObservable().subscribe(name);
            createStartingWordObservable().subscribe(startingWord);
            createEndingWordObservable().subscribe(endingWord);

            v.populateImage(Observable.just(Uri.parse("asdf")));
            v.populateName(name);
            v.populateStartingWord(startingWord);
            v.populateEndingWord(endingWord);
        }

    }

    @Override
    public CharSequence getTitle() {
        return "";
    }

}