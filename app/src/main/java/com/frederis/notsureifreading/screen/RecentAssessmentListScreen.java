package com.frederis.notsureifreading.screen;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.frederis.notsureifreading.CoreBlueprint;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.TransitionScreen;
import com.frederis.notsureifreading.actionbar.DrawerPresenter;
import com.frederis.notsureifreading.actionbar.ToolbarOwner;
import com.frederis.notsureifreading.animation.Transition;
import com.frederis.notsureifreading.model.Assessment;
import com.frederis.notsureifreading.model.Assessments;
import com.frederis.notsureifreading.model.RecentAssessment;
import com.frederis.notsureifreading.model.Student;
import com.frederis.notsureifreading.model.StudentPopupInfo;
import com.frederis.notsureifreading.model.Students;
import com.frederis.notsureifreading.util.RecentAssessmentsCreator;
import com.frederis.notsureifreading.view.RecentAssessmentListView;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.PopupPresenter;
import mortar.ViewPresenter;
import rx.Observable;
import rx.functions.Func1;

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
        Observable<ArrayList<RecentAssessment>> provideRecentAssessments(RecentAssessmentsCreator recentAssessmentsCreator,
                                                                         Observable<ArrayList<Assessment>> allAssessments) {
            return allAssessments.map(recentAssessmentsCreator);
        }

        @Provides
        Observable<ArrayList<Student>> provideStudents(Students students) {
            return students.getAll();
        }

    }

    @Singleton
    public static class Presenter extends ViewPresenter<RecentAssessmentListView> {

        private final Flow mFlow;
        private final Observable<ArrayList<RecentAssessment>> mAssessments;
        private final Observable<ArrayList<Student>> mStudents;
        private final DrawerPresenter mDrawerPresenter;
        private final ToolbarOwner mActionBar;
        private final PopupPresenter<StudentPopupInfo, Long> mStudentSelector;

        @Inject
        Presenter(Flow flow, Observable<ArrayList<RecentAssessment>> assessments, Observable<ArrayList<Student>> students, DrawerPresenter drawerPresenter, ToolbarOwner actionBar) {
            mFlow = flow;
            mAssessments = assessments;
            mStudents = students;
            mDrawerPresenter = drawerPresenter;
            mActionBar = actionBar;
            mStudentSelector = new PopupPresenter<StudentPopupInfo, Long>() {
                @Override
                protected void onPopupResult(Long result) {
                    if (result != 0L) {
                        mFlow.goTo(new PerformAssessmentScreen(result));
                    }
                }
            };
        }

        public Observable<StudentPopupInfo> createInfoObservable() {
            return mStudents.map(new Func1<ArrayList<Student>, StudentPopupInfo>() {
                @Override
                public StudentPopupInfo call(ArrayList<Student> students) {
                    ArrayList<StudentPopupInfo.StudentData> data = new ArrayList<>();

                    for (Student student : students) {
                        data.add(new StudentPopupInfo.StudentData(student.getId(), student.getName()));
                    }

                    return new StudentPopupInfo(data);
                }
            });
        }

        @Override
        public void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            final RecentAssessmentListView view = getView();

            if (view == null) return;

            mActionBar.setConfig(new ToolbarOwner.Config.Builder()
                    .withShowHomeEnabled(true)
                    .withUpEnabled(true)
                    .withTitleResId(R.string.assessments)
                    .withElevationDimensionResId(R.dimen.toolbar_elevation)
                    .withActions(new ToolbarOwner.MenuActions(R.menu.recent_assessment_list, new ToolbarOwner.MenuActions.Callback() {

                        @Override
                        public void onConfigureOptionsMenu(Menu menu) {
                            SearchView searchView = (SearchView) menu.findItem(R.id.search_assessments).getActionView();
                            searchView.setIconifiedByDefault(false);
                            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                                @Override
                                public boolean onClose() {
                                    view.showAssessments(mAssessments);
                                    return true;
                                }
                            });
                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String s) {
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(final String s) {
                                    if (s.length() >= 3) {
                                        view.showAssessments(mAssessments.map(new Func1<ArrayList<RecentAssessment>, ArrayList<RecentAssessment>>() {
                                            @Override
                                            public ArrayList<RecentAssessment> call(ArrayList<RecentAssessment> recentAssessments) {
                                                ArrayList<RecentAssessment> filtered = new ArrayList<>();

                                                for (RecentAssessment assessment : recentAssessments) {
                                                    if (assessment.studentName.toLowerCase().contains(s.toLowerCase())) {
                                                        filtered.add(assessment);
                                                    }
                                                }

                                                return filtered;
                                            }
                                        }));
                                    } else {
                                        view.showAssessments(mAssessments);
                                    }

                                    return true;
                                }
                            });
                        }

                        @Override
                        public boolean onMenuItemSelected(MenuItem menuItem) {
                            return false;
                        }
                    }))
                    .build());

            mDrawerPresenter.setConfig(new DrawerPresenter.Config(true, DrawerLayout.LOCK_MODE_UNLOCKED));

            mStudentSelector.takeView(view.getSelectStudentPopup());

            view.populateAddAssessmentButton(createInfoObservable());
            view.showAssessments(mAssessments);
        }

        @Override
        public void dropView(RecentAssessmentListView view) {
            mStudentSelector.dropView(view.getSelectStudentPopup());
            super.dropView(view);
        }

        public void onAssessmentSelected(long id) {
            mFlow.goTo(new AssessmentScreen(id));
        }

        public void onAddAssessmentSelected(StudentPopupInfo info) {
            mStudentSelector.show(info);
        }

    }

}