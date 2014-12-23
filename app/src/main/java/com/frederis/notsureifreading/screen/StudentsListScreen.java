package com.frederis.notsureifreading.screen;

import android.os.Bundle;

import com.frederis.notsureifreading.MainBlueprint;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.model.Student;
import com.frederis.notsureifreading.model.Students;
import com.frederis.notsureifreading.view.StudentListView;
import com.frederis.notsureifreading.view.StudentRecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;
import rx.Observable;

@Layout(R.layout.student_list_view)
public class StudentsListScreen implements Blueprint {

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = StudentListView.class, addsTo = MainBlueprint.Module.class)
    static class Module {

        @Provides
        Observable<ArrayList<Student>> provideStudents(Students students) {
            return students.getAll();
        }

    }

    @Singleton
    public static class Presenter extends ViewPresenter<StudentListView> {

        private final Flow mFlow;
        private final Observable<ArrayList<Student>> mStudents;

        @Inject
        Presenter(Flow flow,
                  Observable<ArrayList<Student>> students) {
            mFlow = flow;
            mStudents = students;
        }

        @Override
        public void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            StudentListView view = getView();
            if (view == null) return;

            view.showStudents(mStudents);
        }

        public void onStudentSelected(long id) {
            mFlow.goTo(new EditStudentScreen(id));
        }

    }

}
