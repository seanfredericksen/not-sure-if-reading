package com.frederis.notsureifreading.database.cursor;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.frederis.notsureifreading.database.table.WordTable;

import javax.inject.Inject;

import mortar.Mortar;

public class WordCursor extends CursorWrapper {

    @Inject WordTable mWordTable;

    public WordCursor(Context context, Cursor cursor) {
        super(cursor);

        Mortar.inject(context, this);
    }

    public long getId() {
        return getLong(getColumnIndex(mWordTable.getIdColumnName()));
    }

    public String getText() {
        return getString(getColumnIndex(mWordTable.getWordColumn()));
    }

}
