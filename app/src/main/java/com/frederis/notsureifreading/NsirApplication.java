package com.frederis.notsureifreading;

import android.app.Application;

import dagger.ObjectGraph;
import mortar.Mortar;
import mortar.MortarScope;

public class NsirApplication extends Application {

    private MortarScope mRootScope;

    @Override
    public void onCreate() {
        super.onCreate();

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