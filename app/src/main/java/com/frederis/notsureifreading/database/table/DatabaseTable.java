package com.frederis.notsureifreading.database.table;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

abstract public class DatabaseTable {

    abstract public void onCreate(SQLiteDatabase database);

    private String mTableName;

    public DatabaseTable(final String tableName) {
        mTableName = tableName;
    }

    public String getTableName() {
        return mTableName;
    }

    public String getIdColumnName() {
        return "_id";
    }

    public String getIdReference() {
        return String.format("%s(%s)", getTableName(), getIdColumnName());
    }

    public static class Builder {

        private SQLiteDatabase mDatabase;
        private String mTableName;
        private List<DatabaseColumn> mColumns;

        public Builder(SQLiteDatabase database) {
            mDatabase = database;
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

        public void create() {
            mDatabase.execSQL(String.format("CREATE TABLE %s (%s)",
                    mTableName, getColumnsString()));

            for (DatabaseColumn column : mColumns) {
                if (column.isIndexed()) {
                    mDatabase.execSQL("CREATE INDEX " + column.getIndexString(mTableName));
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
