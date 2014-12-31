package com.frederis.notsureifreading.view;

import com.frederis.notsureifreading.MainActivity;
import com.frederis.notsureifreading.activity.AppContainer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                MainActivity.class
        },
        complete = false,
        library = true
)
public class UiModule {
    @Provides @Singleton
    AppContainer provideAppContainer() {
        return AppContainer.DEFAULT;
    }

    @Provides @Singleton ActivityHierarchyServer provideActivityHierarchyServer() {
        return ActivityHierarchyServer.NONE;
    }
}