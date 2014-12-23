package com.frederis.notsureifreading;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import dagger.ObjectGraph;
import mortar.Mortar;
import mortar.MortarScope;

public class NsirApplication extends Application {

    private static final String KEY_HAS_WRITTEN_WORDS = "hasWrittenWords";

    private MortarScope mRootScope;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        preferences.getBoolean(KEY_HAS_WRITTEN_WORDS, false);

        mRootScope =
                Mortar.createRootScope(BuildConfig.DEBUG, ObjectGraph.create(new ApplicationModule(this)));
    }

    @Override
    public Object getSystemService(String name) {
        if (Mortar.isScopeSystemService(name)) {
            return mRootScope;
        }
        return super.getSystemService(name);
    }

}