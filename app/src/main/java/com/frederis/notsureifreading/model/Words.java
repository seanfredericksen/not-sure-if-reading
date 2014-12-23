package com.frederis.notsureifreading.model;

import android.content.ContentValues;
import android.content.Context;

import com.frederis.notsureifreading.ForApplication;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.database.NsirDatabase;
import com.frederis.notsureifreading.database.table.WordTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Words {

    private Context mContext;
    private WordTable mWordTable;
    private NsirDatabase mDatabase;

    @Inject
    public Words(@ForApplication Context context, WordTable wordTable, NsirDatabase database) {
        mContext = context;
        mWordTable = wordTable;
        mDatabase = database;
    }

    public void writeDefaultWords() {
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.sight_words);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String strLine;
            while ((strLine = reader.readLine()) != null) {
                addWordToDatabase(strLine);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to write default words");
        }
    }

    private void addWordToDatabase(String word) {
        final ContentValues values = new ContentValues();
        values.put(mWordTable.getWordColumn(), word);

        mDatabase.getWritableDatabase().insert(mWordTable.getTableName(), null, values);
    }

}
