package com.frederis.notsureifreading;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.frederis.notsureifreading.actionbar.ToolbarOwner;
import com.frederis.notsureifreading.model.Words;
import com.frederis.notsureifreading.screen.DrawerScreen;
import com.frederis.notsureifreading.screen.StudentsListScreen;
import com.frederis.notsureifreading.util.FlowOwner;
import com.frederis.notsureifreading.view.CoreView;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import flow.Flow;
import flow.Parcer;
import mortar.Blueprint;

public class CoreBlueprint implements Blueprint {


    public CoreBlueprint() {
    }

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(
            injects = {CoreView.class},
            addsTo = ApplicationModule.class,
            library = true
    )
    public class Module {
        @Provides
        @MainScope
        Flow provideFlow(Presenter presenter) {
            return presenter.getFlow();
        }
    }

    @Singleton
    public static class Presenter extends FlowOwner<Blueprint, CoreView> {

        private final Context context;
        private final Words words;
        private final ToolbarOwner toolbarOwner;

        @Inject
        Presenter(@ForApplication Context context, Words words, Parcer<Object> flowParcer, ToolbarOwner toolbarOwner) {
            super(flowParcer);

            this.context = context;
            this.words = words;
            this.toolbarOwner = toolbarOwner;
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
        protected Blueprint getFirstScreen() {
            return new StudentsListScreen();
        }

        @Override
        protected Blueprint getDrawerScreen() {
            return new DrawerScreen();
        }

    }

}