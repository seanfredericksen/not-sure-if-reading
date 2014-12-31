package com.frederis.notsureifreading.database;

import android.app.Application;
import android.content.SharedPreferences;

import com.frederis.notsureifreading.database.cursor.AssessmentCursor;
import com.frederis.notsureifreading.database.cursor.StudentCursor;
import com.frederis.notsureifreading.database.cursor.WordCursor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

@Module(
        injects = {StudentCursor.class, WordCursor.class, AssessmentCursor.class},
        complete = false,
        library = true
)
public final class DataModule {

    @Provides @Singleton SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences("nsirApplication", MODE_PRIVATE);
    }

}