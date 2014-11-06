package com.frederis.notsureifreading.activity;

import android.app.Activity;
import android.os.Bundle;

import mortar.Blueprint;
import mortar.Mortar;
import mortar.MortarActivityScope;
import mortar.MortarScope;

public abstract class BaseActivity extends Activity {
    private MortarActivityScope activityScope;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MortarScope parentScope = Mortar.getScope(getApplication());
        activityScope = Mortar.requireActivityScope(parentScope, getBlueprint());
        activityScope.onCreate(savedInstanceState);
    }

    @Override public Object getSystemService(String name) {
        if (Mortar.isScopeSystemService(name)) {
            return activityScope;
        }
        return super.getSystemService(name);
    }

    /**
     * Return the {@link Blueprint} that defines the {@link MortarScope} for this activity.
     */
    protected abstract Blueprint getBlueprint();

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        activityScope.onSaveInstanceState(outState);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            MortarScope parentScope = Mortar.getScope(getApplication());
            parentScope.destroyChild(activityScope);
            activityScope = null;
        }
    }
}