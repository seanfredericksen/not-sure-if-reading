package com.frederis.notsureifreading.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.frederis.notsureifreading.database.table.DatabaseTable;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public abstract class DatabaseDao<T extends DatabaseDao.DatabaseObject, R extends DatabaseTable, C extends CursorWrapper, Q extends DatabaseDao.Query<C>> {

    private PublishSubject<Long> mUpdater = PublishSubject.create();

    private NsirDatabase mDatabase;
    private R mTable;

    public DatabaseDao(NsirDatabase database, R table) {
        mDatabase = database;
        mTable = table;
    }

    public long updateOrInsert(T value) {
        long id = (value.getId() == 0L)
                ? insert(buildValues(value))
                : update(value.getId(), buildValues(value));

        notifyOfUpdates(id);

        return id;
    }

    protected Observable<ArrayList<T>> queryForSet(final Q query) {
        BehaviorSubject<ArrayList<T>> subject = BehaviorSubject.create();

        buildMultiItemObservable(query).subscribe(subject);
        mUpdater.flatMap(new Func1<Long, Observable<ArrayList<T>>>() {
            @Override
            public Observable<ArrayList<T>> call(Long aLong) {
                return buildMultiItemObservable(query);
            }
        }).subscribe(subject);

        return subject.asObservable();
    }

    protected Observable<T> queryForItem(final long itemId, final Q query) {
        BehaviorSubject<T> subject = BehaviorSubject.create();

        buildSingleItemObservable(query).subscribe(subject);
        mUpdater.filter(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long value) {
                return value == itemId;
            }
        }).flatMap(new Func1<Long, Observable<T>>() {
            @Override
            public Observable<T> call(Long aLong) {
                return buildSingleItemObservable(query);
            }
        }).subscribe(subject);

        return subject.asObservable();
    }

    private Observable<ArrayList<T>> buildMultiItemObservable(final Q query) {
        return Observable.create(new Observable.OnSubscribe<ArrayList<T>>() {
                                     @Override
                                     public void call(Subscriber<? super ArrayList<T>> subscriber) {
                                         try {
                                             Thread.sleep(10000);
                                         } catch (Exception e) {
                                         }

                                         C cursor = query.execute();

                                         ArrayList<T> objects = buildObjectList(cursor);

                                         cursor.close();

                                         subscriber.onNext(objects);
                                     }
                                 }

        ).

                observeOn(Schedulers.io()

                ).

                subscribeOn(Schedulers.io()

                );
    }

    private Observable<T> buildSingleItemObservable(final Q query) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                C cursor = query.execute();

                T object = buildObject(cursor);

                cursor.close();

                subscriber.onNext(object);
            }
        }).observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    }

    private void notifyOfUpdates(long id) {
        mUpdater.onNext(id);
    }

    private long insert(ContentValues values) {
        return mDatabase.getWritableDatabase().insert(mTable.getTableName(), null, values);
    }

    private long update(long id, ContentValues values) {
        return mDatabase.getWritableDatabase().update(mTable.getTableName(),
                values,
                mTable.getIdColumnName() + " = ?",
                new String[]{Long.toString(id)});
    }

    public Q queryBuilder() {
        return createQuery(mTable, mDatabase);
    }

    protected abstract ContentValues buildValues(T value);

    protected abstract Q createQuery(R table, NsirDatabase database);

    protected abstract T buildObject(C cursor);

    protected abstract ArrayList<T> buildObjectList(C cursor);

    public static interface DatabaseObject {
        long getId();
    }

    public static interface CursorBuilder<T extends CursorWrapper> {
        T buildCursor(Cursor cursor);
    }

    public static class Query<T extends CursorWrapper> {

        private DatabaseTable mTable;
        private CursorBuilder<T> mCursorBuilder;
        private NsirDatabase mDatabase;

        private ArrayList<String> mColumns = new ArrayList<>();
        private String mSelection = "";
        private ArrayList<String> mSelectionArgs = new ArrayList<>();
        private String mOrderBy;

        public Query(DatabaseTable table, CursorBuilder<T> cursorBuilder, NsirDatabase database) {
            mTable = table;
            mCursorBuilder = cursorBuilder;
            mDatabase = database;
        }

        protected void setOrderBy(String orderBy) {
            mOrderBy = orderBy;
        }

        protected void addColumn(String columnName) {
            mColumns.add(columnName);
        }

        protected void addSelection(String selection, String selectionArg) {
            mSelection += mSelection.isEmpty() ? selection : " AND " + selection;
            mSelectionArgs.add(selectionArg);
        }

        public T execute() {
            return mCursorBuilder.buildCursor(mDatabase.getReadableDatabase()
                    .query(mTable.getTableName(),
                            mColumns.toArray(new String[mColumns.size()]),
                            mSelection,
                            mSelectionArgs.toArray(new String[mSelectionArgs.size()]),
                            null,
                            null,
                            mOrderBy));
        }

    }

}
