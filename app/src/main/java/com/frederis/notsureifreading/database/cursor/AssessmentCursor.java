package com.frederis.notsureifreading.database.cursor;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.frederis.notsureifreading.database.table.AssessmentTable;

import javax.inject.Inject;

import mortar.Mortar;

public class AssessmentCursor extends CursorWrapper {

    @Inject AssessmentTable mAssessmentTable;

    public AssessmentCursor(Context context, Cursor cursor) {
        super(cursor);

        Mortar.inject(context, this);
    }

    public long getId() {
        return getLong(getColumnIndex(mAssessmentTable.getIdColumnName()));
    }

    public long getStudentId() {
        return getLong(getColumnIndex(mAssessmentTable.getStudentColumn()));
    }

    public long getDate() {
        return getLong(getColumnIndex(mAssessmentTable.getDateColumn()));
    }

    public long getStartingWord() {
        return getLong(getColumnIndex(mAssessmentTable.getStartingWordColumn()));
    }

    public long getEndingWord() {
        return getLong(getColumnIndex(mAssessmentTable.getEndingWordColumn()));
    }

    public long get1To50Results() {
        return getLong(getColumnIndex(mAssessmentTable.get1To50ResultsColumn()));
    }

    public long get51To100Results() {
        return getLong(getColumnIndex(mAssessmentTable.get51to100ResultsColumn()));
    }

}
