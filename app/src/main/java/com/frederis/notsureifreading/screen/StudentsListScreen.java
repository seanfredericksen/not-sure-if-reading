package com.frederis.notsureifreading.screen;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import com.frederis.notsureifreading.CoreBlueprint;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.TransitionScreen;
import com.frederis.notsureifreading.actionbar.DrawerPresenter;
import com.frederis.notsureifreading.actionbar.ToolbarOwner;
import com.frederis.notsureifreading.animation.Transition;
import com.frederis.notsureifreading.model.Student;
import com.frederis.notsureifreading.model.Students;
import com.frederis.notsureifreading.view.StudentListView;

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
@Transition({R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right})
public class StudentsListScreen extends TransitionScreen implements Blueprint {

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = StudentListView.class, addsTo = CoreBlueprint.Module.class)
    static class Module {

        @Provides
        Observable<ArrayList<Student>> provideStudents(Students students) {
            return students.getAll();
        }

    }

    @Singleton
    public static class Presenter extends ViewPresenter<StudentListView> {

        private final DrawerPresenter mDrawerPresenter;
        private final ToolbarOwner mToolbar;
        private final Flow mFlow;
        private final Observable<ArrayList<Student>> mStudents;

        @Inject
        Presenter(DrawerPresenter drawerPresenter,
                  ToolbarOwner toolbar,
                  Flow flow,
                  Observable<ArrayList<Student>> students) {
            mDrawerPresenter = drawerPresenter;
            mToolbar = toolbar;
            mFlow = flow;
            mStudents = students;
        }

        @Override
        public void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            StudentListView view = getView();
            if (view == null) return;

            mToolbar.setConfig(new ToolbarOwner.Config(true, true, "Students", null, R.dimen.toolbar_elevation));
            mDrawerPresenter.setConfig(new DrawerPresenter.Config(true, DrawerLayout.LOCK_MODE_UNLOCKED));

            view.showStudents(mStudents);
        }

        public void onStudentSelected(long id) {
            mFlow.goTo(new EditStudentScreen(id));
        }

    }

}
