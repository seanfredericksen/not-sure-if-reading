package com.frederis.notsureifreading.database.cursor;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.frederis.notsureifreading.database.table.LogTable;

import javax.inject.Inject;

import mortar.Mortar;

public class LogCursor extends CursorWrapper {

    @Inject LogTable mLogTable;

    public LogCursor(Context context, Cursor cursor) {
        super(cursor);

        Mortar.inject(context, this);
    }

    public long getId() {
        return getLong(getColumnIndex(mLogTable.getIdColumnName()));
    }

    public String getMessage() {
        return getString(getColumnIndex(mLogTable.getMessageColumn()));
    }

    public long getDate() {
        return getLong(getColumnIndex(mLogTable.getDateColumn()));
    }

}
