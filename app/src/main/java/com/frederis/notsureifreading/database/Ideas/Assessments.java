package com.frederis.notsureifreading.database.Ideas;

import java.util.ArrayList;

import rx.Observable;

@DatabaseTable(name = "assessment")
public class Assessments {

    @ForeignIdColumn(foreignTable = Students.class, foreignColumn = DatabaseTable.ID)
    public static final String STUDENT = "student";

    @IntColumn
    public static final String DATE = "date";

    @ForeignIdColumn(foreignTable = Words.class, foreignColumn = DatabaseTable.ID)
    public static final String STARTING_WORD = "startingWord";

    @ForeignIdColumn(foreignTable = Words.class, foreignColumn = DatabaseTable.ID)
    public static final String ENDING_WORD = "endingWord";

    @IntColumn
    public static final String ONE_TO_FIFTY_RESULTS = "oneToFiftyResults";

    @IntColumn
    public static final String FIFTY_ONE_TO_ONE_HUNDRED_RESULTS = "fiftyOneToOneHundredResults";

    public interface Assessment extends DatabaseObject {
        @ColumnLink(STUDENT) long getStudentId();
        @ColumnLink(DATE) long getDate();
        @ColumnLink(STARTING_WORD) long getStartingWord();
        @ColumnLink(ENDING_WORD) long getEndingWord();
        @ColumnLink(ONE_TO_FIFTY_RESULTS) long getOneToFiftyResults();
        @ColumnLink(FIFTY_ONE_TO_ONE_HUNDRED_RESULTS) long getFiftyOneToOneHundredResults();
    }

    public interface Model {
        @MultipleItemRetriever
        @Columns({DatabaseTable.ID,
                  STUDENT,
                  DATE,
                  STARTING_WORD,
                  ENDING_WORD})
        @SortDesc(DATE)
        Observable<ArrayList<Assessment>> getAllAssessments();

        @SingleItemRetriever
        @Columns({DatabaseTable.ID,
                  STUDENT,
                  DATE,
                  STARTING_WORD,
                  ENDING_WORD,
                  ONE_TO_FIFTY_RESULTS,
                  FIFTY_ONE_TO_ONE_HUNDRED_RESULTS})
        @Select(DatabaseTable.ID + " = ?")
        Observable<Assessment> getAssessment(@SelectArg long assessmentId);

        @ItemUpdater
        void updateOrInsert(Assessment assessment);
    }

}
