package com.frederis.notsureifreading.database.Ideas;

import java.util.ArrayList;

import rx.Observable;

@DatabaseTable(name = "log")
public class Logs {

    @TextColumn
    public static final String MESSAGE = "message";

    @IntColumn
    public static final String DATE = "date";

    public interface Log extends DatabaseObject {
        @ColumnLink(MESSAGE) String getMessage();
        @ColumnLink(DATE) long getDate();
    }

    public interface Model {
        @MultipleItemRetriever
        @Columns({DatabaseTable.ID, MESSAGE, DATE})
        @SortDesc(DATE)
        Observable<ArrayList<Log>> getAll();

        @ItemUpdater
        void updateOrInsert(Log log);

    }

}
