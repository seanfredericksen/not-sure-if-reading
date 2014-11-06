package com.frederis.notsureifreading.screen;

/*
 * Copyright 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Bundle;

import com.frederis.notsureifreading.MainBlueprint;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.model.Assessment;
import com.frederis.notsureifreading.model.Assessments;
import com.frederis.notsureifreading.view.RecentAssessmentListView;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;
import rx.Observable;

@Layout(R.layout.recent_assessment_list_view) //
public class RecentAssessmentListScreen implements Blueprint {

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = RecentAssessmentListView.class, addsTo = MainBlueprint.Module.class)
    static class Module {

        @Provides
        List<Assessment> provideAssessments(Assessments assessments) {
            return assessments.getAll();
        }

    }

    @Singleton
    public static class Presenter extends ViewPresenter<RecentAssessmentListView> {

        private final Flow mFlow;
        private final List<Assessment> mAssessments;

        @Inject
        Presenter(Flow flow, List<Assessment> assessments) {
            mFlow = flow;
            mAssessments = assessments;
        }

        @Override
        public void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            RecentAssessmentListView view = getView();
            if (view == null) return;

            view.showAssessments(mAssessments);
        }

        public void onAssessmentSelected(int position) {
            mFlow.goTo(new AssessmentScreen(position));
        }

    }
}