package com.frederis.notsureifreading.actionbar;

import com.frederis.notsureifreading.MainActivity;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(injects = MainActivity.class)
public class ActionBarModule {

    @Provides
    @Singleton
    ToolbarOwner provideActionBarOwner() {
        return new ToolbarOwner();
    }

}
