package com.frederis.notsureifreading.model;

import android.content.ContentValues;
import android.content.Context;

import com.frederis.notsureifreading.ForApplication;
import com.frederis.notsureifreading.database.NsirDatabase;
import com.frederis.notsureifreading.database.cursor.LogCursor;
import com.frederis.notsureifreading.database.table.LogTable;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

@Singleton
public class Logs {

    private PublishSubject<Object> mLogsUpdated = PublishSubject.create();

    private Context mContext;
    private LogTable mLogTable;
    private Students mStudents;
    private NsirDatabase mDatabase;

    @Inject
    public Logs(@ForApplication Context context, LogTable logTable, Students students, NsirDatabase database) {
        mContext = context;
        mLogTable = logTable;
        mStudents = students;
        mDatabase = database;
    }

    public long updateOrInsertLog(Log log) {
        final ContentValues values = new ContentValues();
        values.put(mLogTable.getMessageColumn(), log.getMessage());
        values.put(mLogTable.getDateColumn(), log.getDate());

        long id = (log.getId() == 0L)
                ? insertAssessment(values)
                : updateAssessment(log.getId(), values);

        notifyOfUpdates();

        return id;
    }

    public void writeAssessmentCompletionLogForStudent(long studentId) {
        mStudents.getStudent(studentId)
                .observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(new Action1<Student>() {
            @Override
            public void call(Student student) {
                updateOrInsertLog(new Log("Completed assessment for " + student.getName()));
            }
        });
    }

    private long insertAssessment(ContentValues values) {
        return mDatabase.getWritableDatabase().insert(mLogTable.getTableName(), null, values);
    }

    private long updateAssessment(long studentId, ContentValues values) {
        return mDatabase.getWritableDatabase().update(mLogTable.getTableName(),
                values,
                mLogTable.getIdColumnName() + " = ?",
                new String[]{Long.toString(studentId)});
    }

    private void notifyOfUpdates() {
        mLogsUpdated.onNext(new Object());
    }

    public Observable<ArrayList<Log>> getAll() {
        BehaviorSubject<ArrayList<Log>> assessments = BehaviorSubject.create(new ArrayList<Log>());

        getLogList().subscribe(assessments);
        mLogsUpdated.flatMap(new Func1<Object, Observable<ArrayList<Log>>>() {
            @Override
            public Observable<ArrayList<Log>> call(Object o) {
                return getLogList();
            }
        }).subscribe(assessments);

        return assessments;
    }

    private Observable<ArrayList<Log>> getLogList() {
        return Observable.create(new Observable.OnSubscribe<ArrayList<Log>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Log>> subscriber) {
                subscriber.onNext(readLogs());
            }
        });
    }

    private ArrayList<Log> readLogs() {
        LogCursor cursor = new LogCursor(mContext,
                mDatabase.getReadableDatabase().query(mLogTable.getTableName(),
                        new String[]{mLogTable.getIdColumnName(),
                                mLogTable.getMessageColumn(),
                                mLogTable.getDateColumn()},
                        null,
                        null,
                        null,
                        null,
                        mLogTable.getDateColumn() + " DESC"));

        ArrayList<Log> logs = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                logs.add(constructLog(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return logs;
    }

    private Log constructLog(LogCursor cursor) {
        return new Log(cursor.getId(),
                cursor.getMessage(),
                cursor.getDate());
    }

}
