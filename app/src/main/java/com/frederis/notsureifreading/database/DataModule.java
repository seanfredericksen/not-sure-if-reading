package com.frederis.notsureifreading.database;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.frederis.notsureifreading.ForApplication;
import com.frederis.notsureifreading.database.cursor.AssessmentCursor;
import com.frederis.notsureifreading.database.cursor.LogCursor;
import com.frederis.notsureifreading.database.cursor.StudentCursor;
import com.frederis.notsureifreading.database.cursor.WordCursor;
import com.frederis.notsureifreading.database.table.AssessmentTable;
import com.frederis.notsureifreading.model.AssessmentModel;
import com.frederis.notsureifreading.model.AssessmentModelImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

@Module(
        injects = {StudentCursor.class, WordCursor.class, AssessmentCursor.class, LogCursor.class},
        complete = false,
        library = true
)
public final class DataModule {

    @Provides @Singleton SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences("nsirApplication", MODE_PRIVATE);
    }

    @Provides @Singleton AssessmentModel provideAssessmentModel(@ForApplication Context context,
                                                                AssessmentTable assessmentTable,
                                                                NsirDatabase database) {
        return new AssessmentModelImpl(context, assessmentTable, database);
    }

}