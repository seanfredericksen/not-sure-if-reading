package com.frederis.notsureifreading.database.table;

import android.database.sqlite.SQLiteDatabase;

import javax.inject.Inject;

public class AssessmentTable extends DatabaseTable {

    private StudentTable mStudentTable;

    @Inject
    public AssessmentTable(StudentTable studentTable) {
        super("assessment");

        mStudentTable = studentTable;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        new Builder(database)
                .withTableName(getTableName())
                .withForeignIdColumn(getStudentColumn(), mStudentTable.getIdReference(), false)
                .withIntegerColumn(getDateColumn(), true)
                .withIntegerColumn(get1To50ResultsColumn(), false)
                .withIntegerColumn(get51to100ResultsColumn(), false)
                .create();
    }

    public String getStudentColumn() {
        return "student";
    }

    public String getDateColumn() {
        return "date";
    }

    public String get1To50ResultsColumn() {
        return "oneToFiftyResults";
    }

    public String get51to100ResultsColumn() {
        return "oneToFiftyResults";
    }

}
