package com.frederis.notsureifreading;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.frederis.notsureifreading.actionbar.ActionBarModule;
import com.frederis.notsureifreading.actionbar.ActionBarOwner;
import com.frederis.notsureifreading.model.Words;
import com.frederis.notsureifreading.screen.RecentAssessmentListScreen;
import com.frederis.notsureifreading.screen.StudentsListScreen;
import com.frederis.notsureifreading.util.FlowOwner;
import com.frederis.notsureifreading.util.TitledBlueprint;
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
    public static class Presenter extends FlowOwner<TitledBlueprint, MainView> {

        private final Context context;
        private final Words words;
        private final ActionBarOwner actionBarOwner;

        @Inject
        Presenter(@ForApplication Context context, Words words, Parcer<Object> flowParcer, ActionBarOwner actionBarOwner) {
            super(flowParcer);

            this.context = context;
            this.words = words;
            this.actionBarOwner = actionBarOwner;
        }

        @Override
        public void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (!preferences.getBoolean("hasWrittenWords", false)) {
                words.writeDefaultWords();
                preferences.edit().putBoolean("hasWrittenWords", true).apply();
            }

        }

        @Override
        public void showScreen(TitledBlueprint newScreen, Flow.Direction direction) {
            boolean hasUp = newScreen instanceof HasParent;

            actionBarOwner.setConfig(new ActionBarOwner.Config(false, hasUp, newScreen.getTitle(), null));

            super.showScreen(newScreen, direction);
        }

        @Override
        protected TitledBlueprint getFirstScreen() {
            return new StudentsListScreen();
        }

    }
}