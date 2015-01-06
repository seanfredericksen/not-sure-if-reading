package com.frederis.notsureifreading.screen;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import com.frederis.notsureifreading.CoreBlueprint;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.TransitionScreen;
import com.frederis.notsureifreading.actionbar.DrawerPresenter;
import com.frederis.notsureifreading.actionbar.ToolbarOwner;
import com.frederis.notsureifreading.animation.Transition;
import com.frederis.notsureifreading.model.Log;
import com.frederis.notsureifreading.model.Logs;
import com.frederis.notsureifreading.view.LogRecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;
import rx.Observable;

@Layout(R.layout.log)
@Transition({R.animator.scale_fade_in, R.animator.scale_fade_out, R.animator.scale_fade_in, R.animator.scale_fade_out})
public class LogScreen extends TransitionScreen implements Blueprint {

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = LogRecyclerView.class, addsTo = CoreBlueprint.Module.class)
    static class Module {

        @Provides
        Observable<ArrayList<Log>> provideLogs(Logs logs) {
            return logs.getAll();
        }

    }

    @Singleton
    public static class Presenter extends ViewPresenter<LogRecyclerView> {

        private final Observable<ArrayList<Log>> mLogs;
        private final DrawerPresenter mDrawerPresenter;
        private final ToolbarOwner mToolbarOwner;

        @Inject
        Presenter(Observable<ArrayList<Log>> logs, DrawerPresenter drawerPresenter, ToolbarOwner toolbarOwner) {
            mLogs = logs;
            mDrawerPresenter = drawerPresenter;
            mToolbarOwner = toolbarOwner;
        }

        @Override
        public void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            final LogRecyclerView view = getView();

            if (view == null) return;

            mToolbarOwner.setConfig(new ToolbarOwner.Config.Builder()
                    .withShowHomeEnabled(true)
                    .withUpEnabled(true)
                    .withTitleResId(R.string.logs)
                    .withElevationDimensionResId(R.dimen.toolbar_elevation)
                    .build());

            mDrawerPresenter.setConfig(new DrawerPresenter.Config(true, DrawerLayout.LOCK_MODE_UNLOCKED));

            view.showLogs(mLogs);
        }

    }

}