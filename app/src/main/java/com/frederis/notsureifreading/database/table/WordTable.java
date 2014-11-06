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
                .withIntegerColumn(getIndexColumn(), true)
                .withStringColumn(getWordColumn(), true, true)
                .create();
    }

    public String getIndexColumn() {
        return "index";
    }

    public String getWordColumn() {
        return "word";
    }


}
