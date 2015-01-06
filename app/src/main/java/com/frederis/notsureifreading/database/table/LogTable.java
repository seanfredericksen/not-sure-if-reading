package com.frederis.notsureifreading.database.table;

import android.database.sqlite.SQLiteDatabase;

import javax.inject.Inject;

public class LogTable extends DatabaseTable {

    @Inject
    public LogTable() {
        super("log");
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        new Builder(database)
                .withTableName(getTableName())
                .withStringColumn(getMessageColumn(), false, false)
                .withIntegerColumn(getDateColumn(), false)
                .create();
    }

    public String getMessageColumn() {
        return "message";
    }

    public String getDateColumn() {
        return "date";
    }

}
