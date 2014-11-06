package com.frederis.notsureifreading;

import android.app.Application;
import android.content.Context;

import com.frederis.notsureifreading.model.Assessments;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import flow.Parcer;

@Module(library = true)
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
        return new GsonParcer<Object>(gson);
    }

    @Provides @Singleton @ForApplication
    public Context provideApplicationContext() {
        return mApplication;
    }

}
