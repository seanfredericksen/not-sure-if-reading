package com.frederis.notsureifreading.model;

import android.content.ContentValues;
import android.content.Context;

import com.frederis.notsureifreading.ForApplication;
import com.frederis.notsureifreading.database.NsirDatabase;
import com.frederis.notsureifreading.database.cursor.AssessmentCursor;
import com.frederis.notsureifreading.database.cursor.StudentCursor;
import com.frederis.notsureifreading.database.table.AssessmentTable;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

@Singleton
public class Assessments {

    private PublishSubject<Object> mAssessmentsUpdated = PublishSubject.create();

    private Context mContext;
    private AssessmentTable mAssessmentTable;
    private NsirDatabase mDatabase;

    @Inject
    public Assessments(@ForApplication Context context, AssessmentTable assessmentTable, NsirDatabase database) {
        mContext = context;
        mAssessmentTable = assessmentTable;
        mDatabase = database;
    }

    public long updateOrInsertAssessment(Assessment assessment) {
        final ContentValues values = new ContentValues();
        values.put(mAssessmentTable.getStudentColumn(), assessment.getStudentId());
        values.put(mAssessmentTable.getDateColumn(), assessment.getDate());
        values.put(mAssessmentTable.getStartingWordColumn(), assessment.getStartingWord());
        values.put(mAssessmentTable.getEndingWordColumn(), assessment.getEndingWord());
        values.put(mAssessmentTable.get1To50ResultsColumn(), assessment.getOneToFiftyResult());
        values.put(mAssessmentTable.get51to100ResultsColumn(), assessment.getFiftyOneToOneHundredResult());

        long id = (assessment.getId() == 0L)
                ? insertAssessment(values)
                : updateAssessment(assessment.getId(), values);

        notifyOfUpdates();

        return id;
    }

    private long insertAssessment(ContentValues values) {
        return mDatabase.getWritableDatabase().insert(mAssessmentTable.getTableName(), null, values);
    }

    private long updateAssessment(long studentId, ContentValues values) {
        return mDatabase.getWritableDatabase().update(mAssessmentTable.getTableName(),
                values,
                mAssessmentTable.getIdColumnName() + " = ?",
                new String[]{Long.toString(studentId)});
    }

    private void notifyOfUpdates() {
        mAssessmentsUpdated.onNext(new Object());
    }

    public Observable<ArrayList<Assessment>> getAll() {
        BehaviorSubject<ArrayList<Assessment>> assessments = BehaviorSubject.create(new ArrayList<Assessment>());

        getAssessmentList().subscribe(assessments);
        mAssessmentsUpdated.flatMap(new Func1<Object, Observable<ArrayList<Assessment>>>() {
            @Override
            public Observable<ArrayList<Assessment>> call(Object o) {
                return getAssessmentList();
            }
        }).subscribe(assessments);

        return assessments;
    }

    private Observable<ArrayList<Assessment>> getAssessmentList() {
        return Observable.create(new Observable.OnSubscribe<ArrayList<Assessment>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Assessment>> subscriber) {
                subscriber.onNext(getAssessments());
            }
        });
    }

    private ArrayList<Assessment> getAssessments() {
        AssessmentCursor cursor = new AssessmentCursor(mContext,
                mDatabase.getReadableDatabase().query(mAssessmentTable.getTableName(),
                        new String[]{mAssessmentTable.getIdColumnName(),
                                mAssessmentTable.getStudentColumn(),
                                mAssessmentTable.getDateColumn(),
                                mAssessmentTable.getStartingWordColumn(),
                                mAssessmentTable.getEndingWordColumn(),
                                mAssessmentTable.get1To50ResultsColumn(),
                                mAssessmentTable.get51to100ResultsColumn()},
                        null,
                        null,
                        null,
                        null,
                        mAssessmentTable.getDateColumn() + " DESC"));

        ArrayList<Assessment> assessments = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                assessments.add(constructAssessment(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();

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

}