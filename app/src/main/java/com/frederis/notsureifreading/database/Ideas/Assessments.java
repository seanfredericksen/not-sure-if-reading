package com.frederis.notsureifreading.database.Ideas;

import com.frederis.notsureifreading.model.Students;
import com.frederis.notsureifreading.model.Words;

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

    @TextColumn
    public static final String FOO = "foo";

    public interface Assessment extends DatabaseObject {
        @ColumnLink(STUDENT) long getStudentId();
        @ColumnLink(DATE) long getDate();
        @ColumnLink(STARTING_WORD) long getStartingWord();
        @ColumnLink(FOO) String getFoo();
    }

    public interface Model {
        @MultipleItemRetriever
        @Columns({DatabaseTable.ID, STUDENT, DATE, STARTING_WORD, FOO})
        @SortDesc(DATE)
        Observable<ArrayList<Assessment>> getAllAssessments();

        @SingleItemRetriever
        @Columns({DatabaseTable.ID, STUDENT, DATE, FOO})
        @Select(DatabaseTable.ID + " = ?")
        Observable<Assessment> getAssessment(@SelectArg long assessmentId);

        @ItemUpdater
        void updateOrInsert(Assessment assessment);
    }

}
