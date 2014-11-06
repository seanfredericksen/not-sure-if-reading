package com.frederis.notsureifreading.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.frederis.notsureifreading.ForApplication;
import com.frederis.notsureifreading.database.table.AssessmentTable;
import com.frederis.notsureifreading.database.table.StudentTable;
import com.frederis.notsureifreading.database.table.WordTable;

import javax.inject.Inject;

public class NsirDatabase extends SQLiteOpenHelper {

    private WordTable mWordTable;
    private StudentTable mStudentTable;
    private AssessmentTable mAssessmentTable;

    @Inject
    public NsirDatabase(@ForApplication Context context,
                        WordTable wordTable,
                        StudentTable studentTable,
                        AssessmentTable assessmentTable) {
        super(context, "nsir.db", null, 1);

        mWordTable = wordTable;
        mStudentTable = studentTable;
        mAssessmentTable = assessmentTable;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mWordTable.onCreate(db);
        mStudentTable.onCreate(db);
        mAssessmentTable.onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase database) {
        database.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}