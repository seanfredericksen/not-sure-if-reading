package com.frederis.notsureifreading.database.table;

import android.database.sqlite.SQLiteDatabase;

import javax.inject.Inject;

public class AssessmentTable extends DatabaseTable {

    private WordTable mWordTable;
    private StudentTable mStudentTable;

    @Inject
    public AssessmentTable(StudentTable studentTable, WordTable wordTable) {
        super("assessment");

        mStudentTable = studentTable;
        mWordTable = wordTable;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        new Builder(database)
                .withTableName(getTableName())
                .withForeignIdColumn(getStudentColumn(), mStudentTable.getIdReference(), false)
                .withIntegerColumn(getDateColumn(), true)
                .withForeignIdColumn(getStartingWordColumn(), mWordTable.getIdReference(), false)
                .withForeignIdColumn(getEndingWordColumn(), mWordTable.getIdReference(), false)
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

    public String getStartingWordColumn() {
        return "startingWord";
    }

    public String getEndingWordColumn() {
        return "endingWord";
    }

    public String get1To50ResultsColumn() {
        return "oneToFiftyResults";
    }

    public String get51to100ResultsColumn() {
        return "fiftyOneToOneHundredResults";
    }

}
