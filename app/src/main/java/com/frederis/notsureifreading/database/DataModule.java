package com.frederis.notsureifreading.database;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;

import com.frederis.notsureifreading.database.Ideas.Assessments;
import com.frederis.notsureifreading.database.Ideas.DatabaseAdapter;
import com.frederis.notsureifreading.database.Ideas.Logs;
import com.frederis.notsureifreading.database.Ideas.Students;
import com.frederis.notsureifreading.database.Ideas.Words;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;
import static com.frederis.notsureifreading.database.Ideas.Assessments.Assessment;
import static com.frederis.notsureifreading.database.Ideas.Logs.Log;
import static com.frederis.notsureifreading.database.Ideas.Students.Student;
import static com.frederis.notsureifreading.database.Ideas.Words.Word;

@Module(
        complete = false,
        library = true
)
public final class DataModule {

    @Provides
    @Singleton
    DatabaseAdapter provideDatabaseAdapter() {
        return new DatabaseAdapter();
    }

    @Provides
    @Singleton
    SQLiteOpenHelper provideDatabaseHelper(DatabaseAdapter adapter, Application application) {
        return adapter.createDatabaseOpenHelper(application,
                "nsirAppDb",
                1,
                Assessments.class,
                Students.class,
                Words.class,
                Logs.class);
    }

    @Provides
    @Singleton
    Assessments.Model provideAssessmentModel(DatabaseAdapter adapter, SQLiteOpenHelper databaseHelper) {
        return adapter.createModel(databaseHelper, Assessments.class, Assessment.class, Assessments.Model.class);
    }

    @Provides
    @Singleton
    Logs.Model provideLogModel(DatabaseAdapter adapter, SQLiteOpenHelper databaseHelper) {
        return adapter.createModel(databaseHelper, Logs.class, Log.class, Logs.Model.class);
    }

    @Provides
    @Singleton
    Students.Model provideStudentModel(DatabaseAdapter adapter, SQLiteOpenHelper databaseHelper) {
        return adapter.createModel(databaseHelper, Students.class, Student.class, Students.Model.class);
    }

    @Provides
    @Singleton
    Words.Model provideWordModel(DatabaseAdapter adapter, SQLiteOpenHelper databaseHelper) {
        return adapter.createModel(databaseHelper, Words.class, Word.class, Words.Model.class);
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences("nsirApplication", MODE_PRIVATE);
    }

}