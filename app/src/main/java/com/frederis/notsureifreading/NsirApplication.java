package com.frederis.notsureifreading;

import java.util.concurrent.TimeUnit;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.frederis.notsureifreading.view.ActivityHierarchyServer;

import javax.inject.Inject;

import dagger.ObjectGraph;
import mortar.Mortar;
import mortar.MortarScope;
import timber.log.Timber;

public class NsirApplication extends Application {

    private static final String KEY_HAS_WRITTEN_WORDS = "hasWrittenWords";

    private MortarScope mRootScope;

    @Inject ActivityHierarchyServer activityHierarchyServer;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            //INIT CRASHLYTICS
        }

        buildObjectGraphAndInject();

        registerActivityLifecycleCallbacks(activityHierarchyServer);

    }

    private void buildObjectGraphAndInject() {
        long start = System.nanoTime();

        ObjectGraph objectGraph = ObjectGraph.create(new ApplicationModule(this));
        objectGraph.inject(this);
        mRootScope = Mortar.createRootScope(BuildConfig.DEBUG, objectGraph);

        long diff = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        Timber.i("Global object graph creation took %sms", diff);
    }

    public void rebuildOjectGraphAndInject() {
        Mortar.destroyRootScope(mRootScope);
        buildObjectGraphAndInject();
    }

    public static NsirApplication get(Context context) {
        return (NsirApplication) context.getApplicationContext();
    }

    @Override
    public Object getSystemService(String name) {
        if (Mortar.isScopeSystemService(name)) {
            return mRootScope;
        }
        return super.getSystemService(name);
    }

}