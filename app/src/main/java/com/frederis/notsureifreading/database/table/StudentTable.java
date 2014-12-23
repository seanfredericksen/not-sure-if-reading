package com.frederis.notsureifreading.database.table;

import android.database.sqlite.SQLiteDatabase;

import javax.inject.Inject;

public class StudentTable extends DatabaseTable {

    private WordTable mWordTable;

    @Inject
    public StudentTable(WordTable wordTable) {
        super("student");

        mWordTable = wordTable;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        new Builder(database)
                .withTableName(getTableName())
                .withStringColumn(getFirstNameColumn(), true, true)
                .withStringColumn(getLastNameColumn(), true, true)
                .withForeignIdColumn(getStartingWordColumn(), mWordTable.getIdReference(), false)
                .withForeignIdColumn(getEndingWordColumn(), mWordTable.getIdReference(), false)
                .create();
    }

    public String getFirstNameColumn() {
        return "firstName";
    }

    public String getLastNameColumn() {
        return "lastName";
    }

    public String getStartingWordColumn() {
        return "startingWord";
    }

    public String getEndingWordColumn() {
        return "endingWord";
    }


}
