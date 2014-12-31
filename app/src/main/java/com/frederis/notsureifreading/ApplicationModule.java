package com.frederis.notsureifreading;

import android.app.Application;
import android.content.Context;

import com.frederis.notsureifreading.database.DataModule;
import com.frederis.notsureifreading.database.cursor.AssessmentCursor;
import com.frederis.notsureifreading.database.cursor.StudentCursor;
import com.frederis.notsureifreading.database.cursor.WordCursor;
import com.frederis.notsureifreading.util.ScreenParcer;
import com.frederis.notsureifreading.view.UiModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import flow.Parcer;

@Module(includes = {UiModule.class, DataModule.class, AndroidModule.class}, injects = {NsirApplication.class}, library = true)
public class ApplicationModule {

    private Application mApplication;

    public ApplicationModule(NsirApplication application) {
        mApplication = application;
    }

    @Provides @Singleton
    Gson provideGson() {
        return new GsonBuilder().create();
    }

    @Provides @Singleton
    Parcer<Object> provideParcer(Gson gson) {
        return new ScreenParcer<>();
    }

    @Provides @Singleton Application provideApplication() {
        return mApplication;
    }

}
