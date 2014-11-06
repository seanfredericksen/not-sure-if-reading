package com.frederis.notsureifreading;

import com.frederis.notsureifreading.actionbar.ActionBarModule;
import com.frederis.notsureifreading.actionbar.ActionBarOwner;
import com.frederis.notsureifreading.screen.RecentAssessmentListScreen;
import com.frederis.notsureifreading.util.FlowOwner;
import com.frederis.notsureifreading.view.MainView;

import dagger.Provides;
import flow.Flow;
import flow.HasParent;
import flow.Parcer;

import javax.inject.Inject;
import javax.inject.Singleton;

import mortar.Blueprint;
import rx.functions.Action0;

public class MainBlueprint implements Blueprint {

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module( //
            includes = ActionBarModule.class,
            injects = MainView.class,
            addsTo = ApplicationModule.class, //
            library = true //
    )
    public static class Module {
        @Provides
        @MainScope
        Flow provideFlow(Presenter presenter) {
            return presenter.getFlow();
        }
    }

    @Singleton
    public static class Presenter extends FlowOwner<Blueprint, MainView> {
        private final ActionBarOwner actionBarOwner;

        @Inject
        Presenter(Parcer<Object> flowParcer, ActionBarOwner actionBarOwner) {
            super(flowParcer);
            this.actionBarOwner = actionBarOwner;
        }

        @Override
        public void showScreen(Blueprint newScreen, Flow.Direction direction) {
            boolean hasUp = newScreen instanceof HasParent;
            String title = newScreen.getClass().getSimpleName();
            ActionBarOwner.MenuAction menu =
                    hasUp ? null : new ActionBarOwner.MenuAction("Friends", new Action0() {
                        @Override
                        public void call() {
                        }
                    });
            actionBarOwner.setConfig(new ActionBarOwner.Config(false, hasUp, title, menu));

            super.showScreen(newScreen, direction);
        }

        @Override
        protected Blueprint getFirstScreen() {
            return new RecentAssessmentListScreen();
        }

    }
}