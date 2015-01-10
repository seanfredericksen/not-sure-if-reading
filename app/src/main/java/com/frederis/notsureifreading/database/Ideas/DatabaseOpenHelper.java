package com.frederis.notsureifreading.database.Ideas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private final TableCreator[] mCreators;

    public DatabaseOpenHelper(Context context, String name, int version, TableCreator[] creators) {
        super(context, name, null, version);

        mCreators = creators;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (TableCreator creator : mCreators) {
            creator.onCreate(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (TableCreator creator : mCreators) {
            creator.onUpgrade(db, oldVersion, newVersion);
        }
    }

    public static interface TableCreator {
        void onCreate(SQLiteDatabase db);
        void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }


}
