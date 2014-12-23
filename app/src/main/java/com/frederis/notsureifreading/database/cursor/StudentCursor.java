package com.frederis.notsureifreading.database.cursor;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.frederis.notsureifreading.database.table.StudentTable;

import javax.inject.Inject;

import mortar.Mortar;

public class StudentCursor extends CursorWrapper {

    @Inject StudentTable mStudentTable;

    public StudentCursor(Context context, Cursor cursor) {
        super(cursor);

        Mortar.inject(context, this);
    }

    public long getId() {
        return getLong(getColumnIndex(mStudentTable.getIdColumnName()));
    }

    public String getFirstName() {
        return getString(getColumnIndex(mStudentTable.getFirstNameColumn()));
    }

    public String getLastName() {
        return getString(getColumnIndex(mStudentTable.getLastNameColumn()));
    }

    public long getStartingWord() {
        return getLong(getColumnIndex(mStudentTable.getStartingWordColumn()));

    }

    public long getEndingWord() {
        return getLong(getColumnIndex(mStudentTable.getEndingWordColumn()));
    }

}
