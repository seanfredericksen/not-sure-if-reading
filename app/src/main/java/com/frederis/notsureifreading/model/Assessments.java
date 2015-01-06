package com.frederis.notsureifreading.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.frederis.notsureifreading.ForApplication;
import com.frederis.notsureifreading.database.DatabaseDao;
import com.frederis.notsureifreading.database.NsirDatabase;
import com.frederis.notsureifreading.database.cursor.AssessmentCursor;
import com.frederis.notsureifreading.database.cursor.StudentCursor;
import com.frederis.notsureifreading.database.table.AssessmentTable;
import com.frederis.notsureifreading.database.table.DatabaseTable;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

@Singleton
public class Assessments extends DatabaseDao<Assessment, AssessmentTable, AssessmentCursor, Assessments.Query> {

    private Context mContext;
    private AssessmentTable mAssessmentTable;

    @Inject
    public Assessments(@ForApplication Context context, AssessmentTable assessmentTable, NsirDatabase database) {
        super(database, assessmentTable);

        mContext = context;
        mAssessmentTable = assessmentTable;
    }

    public Observable<ArrayList<Assessment>> getAll() {
        return queryForSet(
                queryBuilder()
                    .withId()
                    .withStudentId()
                    .withDescendingDate()
                    .withStartingWord()
                    .withEndingWord()
                    .withResults()
        );
    }

    public Observable<Assessment> getAssessment(long assessmentId) {
        return queryForItem(assessmentId,
                queryBuilder()
                    .withId(assessmentId)
                    .withStudentId()
                    .withDate()
                    .withStartingWord()
                    .withEndingWord()
                    .withResults()
        );
    }

    @Override
    protected ContentValues buildValues(Assessment assessment) {
        final ContentValues values = new ContentValues();
        values.put(mAssessmentTable.getStudentColumn(), assessment.getStudentId());
        values.put(mAssessmentTable.getDateColumn(), assessment.getDate());
        values.put(mAssessmentTable.getStartingWordColumn(), assessment.getStartingWord());
        values.put(mAssessmentTable.getEndingWordColumn(), assessment.getEndingWord());
        values.put(mAssessmentTable.get1To50ResultsColumn(), assessment.getOneToFiftyResult());
        values.put(mAssessmentTable.get51to100ResultsColumn(), assessment.getFiftyOneToOneHundredResult());

        return values;
    }

    @Override
    protected Query createQuery(AssessmentTable table, NsirDatabase database) {
        return new Query(table, new CursorBuilder(mContext), database);
    }

    @Override
    protected Assessment buildObject(AssessmentCursor cursor) {
        if (cursor.moveToFirst()) {
            return constructAssessment(cursor);
        } else {
            return new Assessment(0L, 0L, 0L, 0L, 0L, 0L, 0L);
        }
    }

    @Override
    protected ArrayList<Assessment> buildObjectList(AssessmentCursor cursor) {
        ArrayList<Assessment> assessments = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                assessments.add(constructAssessment(cursor));
            } while (cursor.moveToNext());
        }

        return assessments;
    }

    private Assessment constructAssessment(AssessmentCursor cursor) {
        return new Assessment(cursor.getId(),
                cursor.getStudentId(),
                cursor.getDate(),
                cursor.getStartingWord(),
                cursor.getEndingWord(),
                cursor.get1To50Results(),
                cursor.get51To100Results());
    }

    private static class CursorBuilder implements DatabaseDao.CursorBuilder<AssessmentCursor> {

        private Context mContext;

        public CursorBuilder(Context context) {
            mContext = context;
        }

        @Override
        public AssessmentCursor buildCursor(Cursor cursor) {
            return new AssessmentCursor(mContext, cursor);
        }
    }

    public static class Query extends DatabaseDao.Query<AssessmentCursor> {

        private AssessmentTable mTable;

        public Query(AssessmentTable table, CursorBuilder cursorBuilder, NsirDatabase database) {
            super(table, cursorBuilder, database);

            mTable = table;
        }

        public Query withId() {
            addColumn(mTable.getIdColumnName());
            return this;
        }

        public Query withId(long id) {
            addSelection(mTable.getIdColumnName() + " = ?", Long.toString(id));
            return withId();
        }

        public Query withStudentId() {
            addColumn(mTable.getStudentColumn());
            return this;
        }

        public Query withDate() {
            addColumn(mTable.getDateColumn());
            return this;
        }

        public Query withStartingWord() {
            addColumn(mTable.getStartingWordColumn());
            return this;
        }

        public Query withEndingWord() {
            addColumn(mTable.getEndingWordColumn());
            return this;
        }

        public Query withResults() {
            addColumn(mTable.get1To50ResultsColumn());
            addColumn(mTable.get51to100ResultsColumn());
            return this;
        }

        public Query withDescendingDate() {
            setOrderBy(mTable.getDateColumn() + " DESC");
            return withDate();
        }
    }


}