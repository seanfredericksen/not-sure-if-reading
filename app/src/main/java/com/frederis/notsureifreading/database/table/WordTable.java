package com.frederis.notsureifreading.database.table;

import android.database.sqlite.SQLiteDatabase;

import javax.inject.Inject;

public class WordTable extends DatabaseTable {

    @Inject
    public WordTable() {
        super("word");
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        new Builder(database)
                .withTableName(getTableName())
                .withStringColumn(getWordColumn(), true, true)
                .create();
    }

    public String getWordColumn() {
        return "word";
    }


}
