package com.frederis.notsureifreading.database.Ideas;

import java.util.ArrayList;

import rx.Observable;

@DatabaseTable(name = "word")
public class Words {

    @TextColumn(isIndexed = true, isUnique = true)
    public static final String NAME = "name";

    public interface Word extends DatabaseObject {
        @ColumnLink(NAME) String getName();
    }

    public interface Model {
        @MultipleItemRetriever
        @Columns({DatabaseTable.ID, NAME})
        @Select({DatabaseTable.ID + " >= ?", DatabaseTable.ID + " <= ?"})
        Observable<ArrayList<Word>> getWordSet(@SelectArg long startingWord, @SelectArg long endingWord);

        @ItemUpdater
        void updateOrInsert(Word word);
    }

}
