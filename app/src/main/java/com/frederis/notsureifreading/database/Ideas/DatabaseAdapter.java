package com.frederis.notsureifreading.database.Ideas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseAdapter {

    private final Map<Class<?>, Map<Method, ModelFieldInfo>> serviceMethodInfoCache = new LinkedHashMap<>();

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
    public <T> T createModel(Class<?> table, Class<T> model) {
        return (T) Proxy.newProxyInstance(model.getClassLoader(), new Class<?>[]{model}, new ModelFieldHandler(getMethodInfoCache(model)));
    }

    private class ModelFieldHandler implements InvocationHandler {

        private final Class tableClass;
        private final Map<Method, ModelFieldInfo> methodDetailsCache;

        ModelFieldHandler(Class tableClass, Map<Method, ModelFieldInfo> methodDetailsCache) {
            this.tableClass = tableClass;
            this.methodDetailsCache = methodDetailsCache;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }

            final ModelFieldInfo methodInfo = getMethodInfo(tableClass, methodDetailsCache, method);

            Method modelMethod = methodInfo.getMethod();


            return null;
        }
    }

    Map<Method, ModelFieldInfo> getMethodInfoCache(Class<?> service) {
        synchronized (serviceMethodInfoCache) {
            Map<Method, ModelFieldInfo> methodInfoCache = serviceMethodInfoCache.get(service);
            if (methodInfoCache == null) {
                methodInfoCache = new LinkedHashMap<>();
                serviceMethodInfoCache.put(service, methodInfoCache);
            }
            return methodInfoCache;
        }
    }

    static ModelFieldInfo getMethodInfo(Class tableClass, Map<Method, ModelFieldInfo> cache, Method method) {
        synchronized (cache) {
            ModelFieldInfo methodInfo = cache.get(method);
            if (methodInfo == null) {
                methodInfo = new ModelFieldInfo(tableClass, method);
                cache.put(method, methodInfo);
            }
            return methodInfo;
        }
    }

    public static class ModelFieldInfo {

        private Class mTableClass;
        private Method mMethod;
        private ModelMethodType mType;
        private Class mObjectClass;

        public ModelFieldInfo(Class tableClass, Method method) {
            mTableClass = tableClass;
            mMethod = method;

            parseMethodType();
        }

        synchronized void init() {

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
