package com.frederis.notsureifreading.model;

import android.content.ContentValues;
import android.content.Context;

import com.frederis.notsureifreading.ForApplication;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.database.NsirDatabase;
import com.frederis.notsureifreading.database.cursor.WordCursor;
import com.frederis.notsureifreading.database.table.StudentTable;
import com.frederis.notsureifreading.database.table.WordTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

@Singleton
public class Words {

    private Context mContext;
    private WordTable mWordTable;
    private Students mStudents;
    private NsirDatabase mDatabase;

    @Inject
    public Words(@ForApplication Context context, WordTable wordTable, Students students, NsirDatabase database) {
        mContext = context;
        mWordTable = wordTable;
        mStudents = students;
        mDatabase = database;
    }

    //TODO - Change this to actually look at how many words exist
    public long getMaxWords() {
        return 300L;
    }

    public void writeDefaultWords() {
        writeDefaultWordSet(R.raw.first_hundred_words);
        writeDefaultWordSet(R.raw.second_hundred_words);
        writeDefaultWordSet(R.raw.third_hundred_words);
    }

    private void writeDefaultWordSet(int rawResourceId) {
        InputStream inputStream = mContext.getResources().openRawResource(rawResourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String strLine;
            while ((strLine = reader.readLine()) != null) {
                addWordToDatabase(strLine);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to write words: " + rawResourceId);
        }
    }

    public Observable<ArrayList<Word>> getCurrentWordsForStudent(long studentId) {
        BehaviorSubject<ArrayList<Word>> words = BehaviorSubject.create(new ArrayList<Word>());

        getCurrentWordListForStudent(studentId).subscribe(words);

        return words;
    }

    private Observable<ArrayList<Word>> getCurrentWordListForStudent(final long studentId) {
        return mStudents.getStudent(studentId).flatMap(new Func1<Student, Observable<ArrayList<Word>>>() {
            @Override
            public Observable<ArrayList<Word>> call(final Student student) {
                return Observable.create(new Observable.OnSubscribe<ArrayList<Word>>() {
                    @Override
                    public void call(Subscriber<? super ArrayList<Word>> subscriber) {
                        subscriber.onNext(getStudentsWords(student));
                    }
                });
            }
        });
    }

    public ArrayList<Word> getStudentsWords(Student student) {
        return getWordSet(student.getStartingWord(), student.getEndingWord());
    }

    public ArrayList<Word> getAssessmentWords(Assessment assessment) {
        return getWordSet(assessment.getStartingWord(), assessment.getEndingWord());
    }

    private ArrayList<Word> getWordSet(long startingWordId, long endingWordId) {
        WordCursor cursor = new WordCursor(mContext,
                mDatabase.getReadableDatabase().query(mWordTable.getTableName(),
                        new String[] {mWordTable.getIdColumnName(),
                                mWordTable.getWordColumn()},
                        mWordTable.getIdColumnName() + " >= ? AND " + mWordTable.getIdColumnName() + " <= ?" ,
                        new String[] {Long.toString(startingWordId), Long.toString(endingWordId)},
                        null,
                        null,
                        null));

        ArrayList<Word> words = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                words.add(constructWord(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return words;
    }

    private Word constructWord(WordCursor cursor) {
        return new Word(cursor.getId(), cursor.getText());
    }

    private void addWordToDatabase(String word) {
        final ContentValues values = new ContentValues();
        values.put(mWordTable.getWordColumn(), word);

        mDatabase.getWritableDatabase().insert(mWordTable.getTableName(), null, values);
    }

}
