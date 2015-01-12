package com.frederis.notsureifreading.database.Ideas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.frederis.notsureifreading.database.cursor.AssessmentCursor;
import com.frederis.notsureifreading.model.Assessment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class DatabaseAdapter {

    private final Map<Class<?>, Map<Method, ModelMethodInfo>> modelMethodInfoCache = new LinkedHashMap<>();
    private final Map<Class<?>, Map<Method, ObjectMethodInfo>> objectMethodInfoCache = new LinkedHashMap<>();

    public SQLiteOpenHelper createDatabaseOpenHelper(Context context, String databaseName, int databaseVersion, Class... tableClasses) {
        List<DatabaseOpenHelper.TableCreator> creators = new ArrayList<>();

        for (Class tableClass : tableClasses) {
            for (Annotation annotation : tableClass.getDeclaredAnnotations()) {
                if (annotation instanceof DatabaseTable) {
                    DatabaseTable table = (DatabaseTable) annotation;
                    Builder builder = new Builder()
                            .withTableName(table.name());

                    for (Field field : tableClass.getDeclaredFields()) {
                        for (Annotation fieldAnnotation : field.getAnnotations()) {
                            if (fieldAnnotation instanceof ForeignIdColumn) {
                                ForeignIdColumn column = (ForeignIdColumn) fieldAnnotation;
                                builder.withForeignIdColumn(extractColumnName(field),
                                        String.format("%s(%s)", extractTableName(column.foreignTable()), column.foreignColumn()),
                                        column.isIndexed());
                            } else if (fieldAnnotation instanceof TextColumn) {
                                TextColumn column = (TextColumn) fieldAnnotation;
                                builder.withStringColumn(extractColumnName(field), column.isIndexed(), column.isUnique());

                            } else if (fieldAnnotation instanceof IntColumn) {
                                IntColumn column = (IntColumn) fieldAnnotation;
                                builder.withIntegerColumn(extractColumnName(field), column.isIndexed());
                            }
                        }
                    }

                    creators.add(new TableCreatorImpl(builder));
                }
            }
        }

        return new DatabaseOpenHelper(context,
                databaseName,
                databaseVersion,
                creators.toArray(new DatabaseOpenHelper.TableCreator[creators.size()]));
    }

    private String extractTableName(Class tableClass) {
        Annotation table = tableClass.getAnnotation(DatabaseTable.class);

        if (table != null) {
            return ((DatabaseTable) table).name();
        }

        throw new IllegalStateException("Referenced table class " + tableClass + " not annotated with DatabaseTable");
    }

    private String extractColumnName(Field field) {
        try {
            return (String) field.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to extra column name from field: " + field);
        }
    }

    @SuppressWarnings("unchecked")
    public <E, O extends DatabaseObject, T> T createModel(SQLiteOpenHelper helper, Class<E> table, Class<O> object, Class<T> model) {
        return (T) Proxy.newProxyInstance(model.getClassLoader(), new Class<?>[]{model}, new ModelFieldHandler<>(helper, table, object, getMethodInfoCache(model)));
    }

    private class ObjectResultHandler implements InvocationHandler {

        private final Map<String, Object> objectReturnValues;
        private final Map<Method, ObjectMethodInfo> objectMethodDetailsCache;

        public ObjectResultHandler(Map<String, Object> objectReturnValues, Map<Method, ObjectMethodInfo> objectMethodDetailsCache) {
            this.objectReturnValues = objectReturnValues;
            this.objectMethodDetailsCache = objectMethodDetailsCache;
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }

            final ObjectMethodInfo info = getObjectMethodInfo(objectMethodDetailsCache, method);

            if (objectReturnValues.containsKey(info.getColumnLinkKey())) {
                return objectReturnValues.get(info.getColumnLinkKey());
            }

            throw new IllegalAccessError("Invoked method: " + method + " but linked column was not included in query");
        }
    }

    private class ModelFieldHandler<E, O extends DatabaseObject> implements InvocationHandler {

        private SQLiteOpenHelper mHelper;
        private final Class<E> tableClass;
        private final Class<O> objectClass;
        private final Map<Method, ModelMethodInfo> methodDetailsCache;

        private final PublishSubject<Long> mUpdater;

        ModelFieldHandler(SQLiteOpenHelper helper, Class<E> tableClass, Class<O> objectClass, Map<Method, ModelMethodInfo> methodDetailsCache) {
            mHelper = helper;
            this.tableClass = tableClass;
            this.objectClass = objectClass;
            this.methodDetailsCache = methodDetailsCache;

            mUpdater = PublishSubject.create();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }

            final ModelMethodInfo methodInfo = getMethodInfo(tableClass, methodDetailsCache, method);

            switch (methodInfo.mType) {
                case MULTI_RETRIEVER:
                    return getMultiRetrieverObservable(args, methodInfo);
                case SINGLE_RETRIEVER:
                    return getSingleRetrieverObservable(args, methodInfo);
                case UPDATER:
                    Log.d("NSIR", "Inserting assessment");
                    updateOrInsert((O) args[0]);
                    return null;
            }

            return null;
        }

        public void updateOrInsert(O value) {
            updateOrInsert(Observable.just(value)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io()));
        }

        private void updateOrInsert(Observable<O> value) {
            value.subscribe(new Action1<O>() {
                @Override
                public void call(O object) {
                    Log.d("NSIR", "Inserting or updating!");
                    long id = (object.getId() == 0L)
                            ? insert(buildValues(object))
                            : update(object.getId(), buildValues(object));

                    notifyOfUpdates(id);
                }
            });
        }

        private ContentValues buildValues(O object) {
            Log.d("NSIR", "Building values");
            Map<Method, ObjectMethodInfo> methodCache = getObjectMethodInfoCache(objectClass);

            ContentValues values = new ContentValues();

            for (Method method : objectClass.getDeclaredMethods()) {
                ColumnLink columnLink = method.getAnnotation(ColumnLink.class);
                if (columnLink != null) {
                    ObjectMethodInfo info = getObjectMethodInfo(methodCache, method);
                    Class<?> returnClass = method.getReturnType();

                    Log.d("NSIR", "Attempting to put value");

                    try {
                        if (returnClass == long.class) {
                            values.put(info.getColumnLinkKey(), (Long) method.invoke(object));
                        } else if (returnClass == String.class) {
                            values.put(info.getColumnLinkKey(), (String) method.invoke(object));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        throw new IllegalAccessError("Illegal access of method: " + method);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        throw new RuntimeException("Error invoking method: " + method);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            Log.d("NSIR", "Returing values");

            return values;
        }

        private void notifyOfUpdates(long id) {
            Log.d("NSIR", "notify of updates");
            mUpdater.onNext(id);
        }

        private long insert(ContentValues values) {
            Log.d("NSIR", "Inserting values");
            return mHelper.getWritableDatabase().insert(getTableName(), null, values);
        }

        private long update(long id, ContentValues values) {
            Log.d("NSIR", "Updating values");
            return mHelper.getWritableDatabase().update(getTableName(),
                    values,
                    DatabaseTable.ID + " = ?",
                    new String[]{Long.toString(id)});
        }

        private Observable<ArrayList<O>> getMultiRetrieverObservable(final Object[] args, final ModelMethodInfo info) {
            return Observable.create(new Observable.OnSubscribe<ArrayList<O>>() {
                @Override
                public void call(final Subscriber<? super ArrayList<O>> subscriber) {
                    info.init();

                    queryForSet(buildQuery(args, info)).subscribe(new Action1<ArrayList<O>>() {
                        @Override
                        public void call(ArrayList<O> os) {
                            subscriber.onNext(os);
                        }
                    });
                }
            }).subscribeOn(Schedulers.io());
        }

        private Observable<O> getSingleRetrieverObservable(final Object[] args, final ModelMethodInfo info) {
            return Observable.create(new Observable.OnSubscribe<O>() {
                @Override
                public void call(final Subscriber<? super O> subscriber) {
                    info.init();

                    queryForItem((Long) args[0], buildQuery(args, info)).subscribe(new Action1<O>() {
                        @Override
                        public void call(O os) {
                            subscriber.onNext(os);
                        }
                    });
                }
            }).subscribeOn(Schedulers.io());
        }


        @SuppressWarnings("unchecked")
        private Query buildQuery(final Object[] args, final ModelMethodInfo info) {
            Query query = new Query(getTableName(), mHelper);

            for (String value : info.mColumns.value()) {
                query.addColumn(value);
            }

            if (info.mSelection != null) {
                String[] selectionValues = info.mSelection.value();
                if (selectionValues.length != args.length) {
                    throw new IllegalStateException("Number of method arguments does not match number of required selecion args");
                }

                for (int i = 0; i < selectionValues.length; i++) {
                    query.addSelection(selectionValues[i], info.mSelectArgHandlers.get(i).extractSelectArg(args[i]));
                }

            }

            return query;
        }

        private String getTableName() {
            DatabaseTable table = tableClass.getAnnotation(DatabaseTable.class);

            if (table == null) {
                throw new IllegalStateException("Provided table class not annotated with DatabaseTable");
            }

            return table.name();
        }

        protected Observable<ArrayList<O>> queryForSet(final Query query) {
            final BehaviorSubject<ArrayList<O>> subject = BehaviorSubject.create();

            final Subscription baseSubscription = buildMultiItemObservable(query).subscribe(subject);
            final Subscription updateSubscription = mUpdater.flatMap(new Func1<Long, Observable<ArrayList<O>>>() {
                @Override
                public Observable<ArrayList<O>> call(Long aLong) {
                    return buildMultiItemObservable(query);
                }
            }).subscribe(subject);

            return subject.doOnUnsubscribe(new Action0() {
                @Override
                public void call() {
                    baseSubscription.unsubscribe();
                    updateSubscription.unsubscribe();
                }
            }).asObservable();
        }

        protected Observable<O> queryForItem(final long itemId, final Query query) {
            BehaviorSubject<O> subject = BehaviorSubject.create();

            buildSingleItemObservable(query).subscribe(subject);
            mUpdater.filter(new Func1<Long, Boolean>() {
                @Override
                public Boolean call(Long value) {
                    return value == itemId;
                }
            }).flatMap(new Func1<Long, Observable<O>>() {
                @Override
                public Observable<O> call(Long aLong) {
                    return buildSingleItemObservable(query);
                }
            }).subscribe(subject);

            return subject.asObservable();
        }


        private Observable<O> buildSingleItemObservable(final Query query) {
            return Observable.create(new Observable.OnSubscribe<O>() {
                @Override
                public void call(Subscriber<? super O> subscriber) {
                    Cursor cursor = query.execute();

                    O object = buildObject(cursor);

                    cursor.close();

                    subscriber.onNext(object);
                }
            }).observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
        }

        private Observable<ArrayList<O>> buildMultiItemObservable(final Query query) {
            return Observable.create(new Observable.OnSubscribe<ArrayList<O>>() {
                                         @Override
                                         public void call(Subscriber<? super ArrayList<O>> subscriber) {
                                             Cursor cursor = query.execute();

                                             ArrayList<O> objects = buildObjectList(cursor);

                                             cursor.close();

                                             subscriber.onNext(objects);
                                         }
                                     }

            ).observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
        }

        private ArrayList<O> buildObjectList(Cursor cursor) {
            ArrayList<O> results = new ArrayList<>();

            if (cursor.moveToFirst()) {
                do {
                    results.add(constructObject(cursor));
                } while (cursor.moveToNext());
            }

            return results;
        }

        private O buildObject(Cursor cursor) {
            if (cursor.moveToFirst()) {
                return constructObject(cursor);
            } else {
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        private O constructObject(Cursor cursor) {
            return (O) Proxy.newProxyInstance(objectClass.getClassLoader(), new Class<?>[]{objectClass}, new ObjectResultHandler(extractCursorValues(cursor), getObjectMethodInfoCache(objectClass)));
        }

        private Map<String, Object> extractCursorValues(Cursor cursor) {
            Map<String, Object> values = new LinkedHashMap<>();

            for (Method method : objectClass.getDeclaredMethods()) {
                Log.d("NSIR", "Extracting wtih method: " + method);

                ColumnLink columnLink = method.getAnnotation(ColumnLink.class);
                if (columnLink != null) {
                    Class<?> returnClass = method.getReturnType();

                    int columnIndex = cursor.getColumnIndex(columnLink.value());
                    if (columnIndex >= 0) {
                        if (returnClass == long.class) {
                            values.put(columnLink.value(), cursor.getLong(cursor.getColumnIndex(columnLink.value())));
                        } else if (returnClass == String.class) {
                            values.put(columnLink.value(), cursor.getString(cursor.getColumnIndex(columnLink.value())));
                        }
                    }
                }
            }

            for (Method method : DatabaseObject.class.getDeclaredMethods()) {
                Log.d("NSIR", "Extracting wtih method: " + method);

                ColumnLink columnLink = method.getAnnotation(ColumnLink.class);
                if (columnLink != null) {
                    Class<?> returnClass = method.getReturnType();

                    if (returnClass == long.class) {
                        values.put(columnLink.value(), cursor.getLong(cursor.getColumnIndex(columnLink.value())));
                    } else if (returnClass == String.class) {
                        values.put(columnLink.value(), cursor.getString(cursor.getColumnIndex(columnLink.value())));
                    }
                }
            }

            return values;
        }

    }

    public static interface CursorBuilder {
        CursorWrapper buildCursor(Cursor cursor);
    }

    public static class Query {

        private String mTableName;
        private SQLiteOpenHelper mDatabase;

        private ArrayList<String> mColumns = new ArrayList<>();
        private String mSelection = "";
        private ArrayList<String> mSelectionArgs = new ArrayList<>();
        private String mOrderBy;

        public Query(String tableName, SQLiteOpenHelper database) {
            mTableName = tableName;
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

        public Cursor execute() {
            return mDatabase.getReadableDatabase()
                    .query(mTableName,
                            mColumns.toArray(new String[mColumns.size()]),
                            mSelection,
                            mSelectionArgs.toArray(new String[mSelectionArgs.size()]),
                            null,
                            null,
                            mOrderBy);
        }

    }


    Map<Method, ModelMethodInfo> getMethodInfoCache(Class<?> service) {
        synchronized (modelMethodInfoCache) {
            Map<Method, ModelMethodInfo> methodInfoCache = modelMethodInfoCache.get(service);
            if (methodInfoCache == null) {
                methodInfoCache = new LinkedHashMap<>();
                modelMethodInfoCache.put(service, methodInfoCache);
            }
            return methodInfoCache;
        }
    }

    Map<Method, ObjectMethodInfo> getObjectMethodInfoCache(Class<?> object) {
        synchronized (objectMethodInfoCache) {
            Map<Method, ObjectMethodInfo> methodInfoCache = objectMethodInfoCache.get(object);
            if (methodInfoCache == null) {
                methodInfoCache = new LinkedHashMap<>();
                objectMethodInfoCache.put(object, methodInfoCache);
            }
            return methodInfoCache;
        }
    }

    static ObjectMethodInfo getObjectMethodInfo(Map<Method, ObjectMethodInfo> cache, Method method) {
        synchronized (cache) {
            ObjectMethodInfo methodInfo = cache.get(method);
            if (methodInfo == null) {
                methodInfo = new ObjectMethodInfo(method);
                cache.put(method, methodInfo);
            }
            return methodInfo;
        }
    }

    static ModelMethodInfo getMethodInfo(Class tableClass, Map<Method, ModelMethodInfo> cache, Method method) {
        synchronized (cache) {
            ModelMethodInfo methodInfo = cache.get(method);
            if (methodInfo == null) {
                methodInfo = new ModelMethodInfo(tableClass, method);
                cache.put(method, methodInfo);
            }
            return methodInfo;
        }
    }

    public static class ObjectMethodInfo {

        private Method mMethod;

        private String mTableFieldName;

        public ObjectMethodInfo(Method method) {
            mMethod = method;

            parseMethodType();
        }

        private void parseMethodType() {
            ColumnLink columnLink = mMethod.getAnnotation(ColumnLink.class);
            if (columnLink == null) {
                throw new IllegalStateException("Object accessor must be linked to a column");
            }

            mTableFieldName = columnLink.value();
        }

        public String getColumnLinkKey() {
            return mTableFieldName;
        }
    }

    public static class ModelMethodInfo {

        private Class mTableClass;
        private Method mMethod;
        private ModelMethodType mType;
        private Class mObjectClass;

        private Columns mColumns;
        private Select mSelection;
        private List<SelectArgHandler> mSelectArgHandlers;

        private boolean mLoaded;

        public ModelMethodInfo(Class tableClass, Method method) {
            mTableClass = tableClass;
            mMethod = method;

            parseMethodType();
        }

        synchronized void init() {
            if (mLoaded) return;

            parseQueryAnnotations();

            mLoaded = true;
        }

        private void parseQueryAnnotations() {
            mColumns = mMethod.getAnnotation(Columns.class);
            mSelection = mMethod.getAnnotation(Select.class);

            if (mColumns == null && !mType.equals(ModelMethodType.UPDATER)) {
                throw new IllegalStateException("Model retriever method must be annotated with columns to include");
            }

            Class<?>[] parameters = mMethod.getParameterTypes();

            if (!mType.equals(ModelMethodType.UPDATER)) {
                mSelectArgHandlers = new ArrayList<>();
                Annotation[][] methodParameterAnnotationArrays = mMethod.getParameterAnnotations();
                Log.d("NSIR", "Checking method: " + mMethod);
                Log.d("NSIR", "Method parameter annotations: " + methodParameterAnnotationArrays[0].length);

                for (int i = 0; i < methodParameterAnnotationArrays.length; i++) {
                    Annotation[] methodParameterAnnotations = methodParameterAnnotationArrays[i];

                    boolean foundSelectArg = false;

                    for (Annotation annotation : methodParameterAnnotations) {
                        Log.d("NSIR", "method param annotation: " + annotation);
                        if (annotation.annotationType() == SelectArg.class) {
                            foundSelectArg = true;
                        }
                    }

                    if (!foundSelectArg) {
                        throw new IllegalStateException("All method parameters on a retriever method must have the SelectArg annotation");
                    }

                    if (parameters[i] == long.class) {
                        mSelectArgHandlers.add(new SelectArgHandler<Long>() {
                            @Override
                            public String extractSelectArg(Long value) {
                                return Long.toString(value);
                            }
                        });
                    } else if (parameters[i] == String.class) {
                        mSelectArgHandlers.add(new SelectArgHandler<String>() {
                            @Override
                            public String extractSelectArg(String value) {
                                return value;
                            }
                        });
                    }
                }

                if (mSelection != null && (mSelectArgHandlers.size() != mSelection.value().length)) {
                    throw new IllegalStateException("Number of selection args does not match the number of selection statements");
                }
            }

        }

        private interface SelectArgHandler<T> {
            String extractSelectArg(T value);

        }

        private void parseMethodType() {
            MultipleItemRetriever multiRetriever = mMethod.getAnnotation(MultipleItemRetriever.class);
            SingleItemRetriever singleItemRetriever = mMethod.getAnnotation(SingleItemRetriever.class);
            ItemUpdater itemUpdater = mMethod.getAnnotation(ItemUpdater.class);

            if (multiRetriever != null) {
                if (singleItemRetriever != null || itemUpdater != null) {
                    throw new IllegalStateException("Cannot annotate multiple retriever method with other model annotations");
                }

                try {
                    mObjectClass = (Class) getArgumentFromParameterizedType(getGenericReturnArg());
                    mType = ModelMethodType.MULTI_RETRIEVER;
                } catch (Exception e) {
                    throw new IllegalStateException("Method annotated with MultipleItemRetriever must have correct return type");
                }
            } else if (singleItemRetriever != null) {
                if (itemUpdater != null) {
                    throw new IllegalStateException("Cannot annotate single retriever method with other model annotations");
                }

                try {
                    mObjectClass = (Class) getGenericReturnArg();
                    mType = ModelMethodType.SINGLE_RETRIEVER;
                } catch (Exception e) {
                    throw new IllegalStateException("Method annotated with SingleItemRetriever must have correct return type");
                }
            } else if (itemUpdater != null) {
                Class<?>[] parameterTypes = mMethod.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new IllegalStateException("Item update model method must have a single parameter");
                }

                mObjectClass = parameterTypes[0];
                mType = ModelMethodType.UPDATER;
            } else {
                throw new IllegalStateException("Model method not a valid type");
            }
        }

        private Type getGenericReturnArg() throws IllegalAccessException {
            return getArgumentFromParameterizedType(mMethod.getGenericReturnType());
        }

        private Type getArgumentFromParameterizedType(Type type) throws IllegalAccessException {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] typeArgs = parameterizedType.getActualTypeArguments();

                return typeArgs[0];
            }

            throw new IllegalAccessException();
        }

        public Method getMethod() {
            return mMethod;
        }

        public static enum ModelMethodType {
            SINGLE_RETRIEVER, MULTI_RETRIEVER, UPDATER
        }

    }

    private class TableCreatorImpl implements DatabaseOpenHelper.TableCreator {

        private Builder mBuilder;

        public TableCreatorImpl(Builder builder) {
            mBuilder = builder;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mBuilder.create(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }

    public static class Builder {

        private String mTableName;
        private List<DatabaseColumn> mColumns;

        public Builder() {
            mColumns = new ArrayList<DatabaseColumn>() {
                {
                    add(new DatabaseColumn("_id", false) {

                        @Override
                        public String getCreationString() {
                            return "_id integer primary key autoincrement";
                        }

                    });
                }
            };
        }

        public Builder withTableName(final String name) {
            mTableName = name;
            return this;
        }

        public Builder withStringColumn(final String name, final boolean indexed, final boolean unique) {
            mColumns.add(new DatabaseColumn(name, indexed) {

                @Override
                public String getCreationString() {
                    return name + " text" + (unique ? " unique" : "");
                }

            });
            return this;
        }

        public Builder withIntegerColumn(final String name, final boolean indexed) {
            mColumns.add(new DatabaseColumn(name, indexed) {

                @Override
                public String getCreationString() {
                    return name + " integer";
                }

            });
            return this;
        }

        public Builder withForeignIdColumn(final String name,
                                           final String reference,
                                           final boolean indexed) {
            mColumns.add(new DatabaseColumn(name, indexed) {

                @Override
                public String getCreationString() {
                    return name + " integer references " + reference;
                }

            });
            return this;
        }

        public void create(SQLiteDatabase db) {
            db.execSQL(String.format("CREATE TABLE %s (%s)",
                    mTableName, getColumnsString()));

            for (DatabaseColumn column : mColumns) {
                if (column.isIndexed()) {
                    db.execSQL("CREATE INDEX " + column.getIndexString(mTableName));
                }
            }
        }

        private String getColumnsString() {
            String output = mColumns.get(0).getCreationString();

            for (int i = 1; i < mColumns.size(); i++) {
                output += ", " + mColumns.get(i).getCreationString();
            }

            return output;
        }

    }

    private static abstract class DatabaseColumn {

        private String mName;
        private boolean mIsIndexed;

        public DatabaseColumn(String name, boolean isIndexed) {
            mName = name;
            mIsIndexed = isIndexed;
        }

        public boolean isIndexed() {
            return mIsIndexed;
        }

        public String getIndexString(String tableName) {
            return String.format("%s_index ON %s(%s)", mName, tableName, mName);
        }

        abstract String getCreationString();

    }

}
