package com.frederis.notsureifreading;

import com.frederis.notsureifreading.actionbar.DrawerPresenter;
import com.frederis.notsureifreading.actionbar.ToolbarOwner;
import com.frederis.notsureifreading.activity.BackPresenter;
import com.frederis.notsureifreading.presenter.ActivityResultPresenter;
import com.frederis.notsureifreading.presenter.ActivityResultRegistrar;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {}, library = true)
public class AndroidModule {

    @Provides @Singleton
    ToolbarOwner provideActionBarPresenter() {
        return new ToolbarOwner();
    }

    @Provides @Singleton
    DrawerPresenter provideDrawerPresenter() {
        return new DrawerPresenter();
    }

    @Provides @Singleton
    ActivityResultRegistrar provideIntentLauncher(ActivityResultPresenter presenter) {
        return presenter;
    }

    @Provides @Singleton
    BackPresenter provideBackPresenter() {
        return new BackPresenter();
    }

}