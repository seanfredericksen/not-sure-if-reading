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
                .withStringColumn(getNameColumn(), true, true)
                .withForeignIdColumn(getStartingWordColumn(), mWordTable.getIdReference(), false)
                .withForeignIdColumn(getEndingWordColumn(), mWordTable.getIdReference(), false)
                .create();
    }

    public String getNameColumn() {
        return "name";
    }

    public String getStartingWordColumn() {
        return "startingWord";
    }

    public String getEndingWordColumn() {
        return "endingWord";
    }


}
