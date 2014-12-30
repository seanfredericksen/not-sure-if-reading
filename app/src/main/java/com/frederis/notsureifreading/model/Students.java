package com.frederis.notsureifreading.model;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.frederis.notsureifreading.ForApplication;
import com.frederis.notsureifreading.database.NsirDatabase;
import com.frederis.notsureifreading.database.cursor.StudentCursor;
import com.frederis.notsureifreading.database.table.StudentTable;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class Students {

    private Context mContext;
    private NsirDatabase mDatabase;
    private StudentTable mStudentTable;

    private PublishSubject<Object> mStudentsUpdated = PublishSubject.create();

    @Inject
    public Students(@ForApplication Context context, NsirDatabase database, StudentTable studentTable) {
        mContext = context;
        mDatabase = database;
        mStudentTable = studentTable;
    }

    public long updateOrInsertStudent(Student student) {
        final ContentValues values = new ContentValues();
        values.put(mStudentTable.getNameColumn(), student.getName());
        values.put(mStudentTable.getStartingWordColumn(), student.getStartingWord());
        values.put(mStudentTable.getEndingWordColumn(), student.getEndingWord());

        Uri imageUri = student.getImageUri();
        if (imageUri != Student.IMAGE_UNCHANGED) {
            values.put(mStudentTable.getImageUriColumn(), imageUri != null ? imageUri.toString() : null);
        }

        long id = (student.getId() == 0L)
                ? insertStudent(values)
                : updateStudent(student.getId(), values);

        notifyOfUpdates();

        return id;
    }

    private long insertStudent(ContentValues values) {
        return mDatabase.getWritableDatabase().insert(mStudentTable.getTableName(), null, values);
    }

    private long updateStudent(long studentId, ContentValues values) {
        return mDatabase.getWritableDatabase().update(mStudentTable.getTableName(),
                values,
                mStudentTable.getIdColumnName() + " = ?",
                new String[]{Long.toString(studentId)});
    }

    private void notifyOfUpdates() {
        mStudentsUpdated.onNext(new Object());
    }

    public Observable<ArrayList<Student>> getAll() {
        BehaviorSubject<ArrayList<Student>> students = BehaviorSubject.create(new ArrayList<Student>());

        getStudentList().subscribe(students);
        mStudentsUpdated.flatMap(new Func1<Object, Observable<ArrayList<Student>>>() {
            @Override
            public Observable<ArrayList<Student>> call(Object o) {
                return getStudentList();
            }
        }).subscribe(students);

        return students;
    }

    public Observable<Student> getStudent(final long studentId) {
        BehaviorSubject<Student> student = BehaviorSubject.create();

        Observable.create(new Observable.OnSubscribe<Student>() {
            @Override
            public void call(Subscriber<? super Student> subscriber) {
                subscriber.onNext(readStudent(studentId));
            }
        }).subscribe(student);

        return student;
    }

    private Observable<ArrayList<Student>> getStudentList() {
//        BehaviorSubject<ArrayList<Student>> students = BehaviorSubject.create();
//
//        Observable.just(new Object()).map(new StudentListBuilder()).subscribe(students);
//
//        return students;

        return Observable.create(new Observable.OnSubscribe<ArrayList<Student>>() {
            @Override
            public void call(Subscriber<? super ArrayList<Student>> subscriber) {
                subscriber.onNext(getStudents());
            }
        });
    }

    private class StudentQuery implements Func1<Long, StudentCursor> {

        @Override
        public StudentCursor call(Long studentId) {
            return new StudentCursor(mContext,
                    mDatabase.getReadableDatabase().query(mStudentTable.getTableName(),
                            new String[]{mStudentTable.getIdColumnName(),
                                    mStudentTable.getNameColumn(),
                                    mStudentTable.getImageUriColumn(),
                                    mStudentTable.getStartingWordColumn(),
                                    mStudentTable.getEndingWordColumn()},
                            mStudentTable.getIdColumnName() + " = ?",
                            new String[]{Long.toString(studentId)},
                            null,
                            null,
                            null));
        }

    }

    private class StudentListBuilder implements Func1<Object, ArrayList<Student>> {

        @Override
        public ArrayList<Student> call(Object object) {
            StudentCursor cursor = new StudentCursor(mContext,
                    mDatabase.getReadableDatabase().query(mStudentTable.getTableName(),
                            new String[]{mStudentTable.getIdColumnName(),
                                    mStudentTable.getNameColumn(),
                                    mStudentTable.getImageUriColumn(),
                                    mStudentTable.getStartingWordColumn(),
                                    mStudentTable.getEndingWordColumn()},
                            null,
                            null,
                            null,
                            null,
                            null));

            ArrayList<Student> students = new ArrayList<Student>();

            if (cursor.moveToFirst()) {
                Log.d("NSIR", "Got students!");
                do {
                    students.add(constructStudent(cursor));
                } while (cursor.moveToNext());
            } else {
                Log.d("NSIR", "No students");
            }

            cursor.close();

            return students;
        }
    }

    private ArrayList<Student> getStudents() {
        StudentCursor cursor = new StudentCursor(mContext,
                mDatabase.getReadableDatabase().query(mStudentTable.getTableName(),
                        new String[]{mStudentTable.getIdColumnName(),
                                mStudentTable.getNameColumn(),
                                mStudentTable.getImageUriColumn(),
                                mStudentTable.getStartingWordColumn(),
                                mStudentTable.getEndingWordColumn()},
                        null,
                        null,
                        null,
                        null,
                        null));

        ArrayList<Student> students = new ArrayList<Student>();

        if (cursor.moveToFirst()) {
            do {
                students.add(constructStudent(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return students;
    }

    private Student readStudent(long studentId) {
        StudentCursor cursor = new StudentCursor(mContext,
                mDatabase.getReadableDatabase().query(mStudentTable.getTableName(),
                        new String[]{mStudentTable.getIdColumnName(),
                                mStudentTable.getNameColumn(),
                                mStudentTable.getImageUriColumn(),
                                mStudentTable.getStartingWordColumn(),
                                mStudentTable.getEndingWordColumn()},
                        mStudentTable.getIdColumnName() + " = ?",
                        new String[]{Long.toString(studentId)},
                        null,
                        null,
                        null));

        Student student = cursor.moveToFirst()
                ? constructStudent(cursor)
                : new Student(0L, "", Uri.EMPTY, 0L, 0L);

        cursor.close();

        return student;
    }

    private class StudentBuilder implements Func1<Long, Student> {

        @Override
        public Student call(Long studentId) {
            StudentCursor cursor = new StudentCursor(mContext,
                    mDatabase.getReadableDatabase().query(mStudentTable.getTableName(),
                            new String[]{mStudentTable.getIdColumnName(),
                                    mStudentTable.getNameColumn(),
                                    mStudentTable.getImageUriColumn(),
                                    mStudentTable.getStartingWordColumn(),
                                    mStudentTable.getEndingWordColumn()},
                            mStudentTable.getIdColumnName() + " = ?",
                            new String[]{Long.toString(studentId)},
                            null,
                            null,
                            null));

            Student student = cursor.moveToFirst()
                    ? constructStudent(cursor)
                    : new Student(0L, "", Uri.EMPTY, 0L, 0L);

            cursor.close();

            return student;
        }

    }

    private Student constructStudent(StudentCursor cursor) {
        return new Student(cursor.getId(),
                cursor.getName(),
                cursor.getImageUri(),
                cursor.getStartingWord(),
                cursor.getEndingWord());
    }

}
