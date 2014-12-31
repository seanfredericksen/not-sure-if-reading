package com.frederis.notsureifreading.activity;

import android.app.Activity;
import android.view.ViewGroup;

import com.frederis.notsureifreading.NsirApplication;

import static butterknife.ButterKnife.findById;

/** An indirection which allows controlling the root container used for each activity. */
public interface AppContainer {
    /** The root {@link android.view.ViewGroup} into which the activity should place its contents. */
    ViewGroup get(Activity activity, NsirApplication app);

    /** An {@link AppContainer} which returns the normal activity content view. */
    AppContainer DEFAULT = new AppContainer() {
        @Override public ViewGroup get(Activity activity, NsirApplication app) {
            return findById(activity, android.R.id.content);
        }
    };
}