package com.frederis.notsureifreading.database.Ideas;

import java.util.ArrayList;

import rx.Observable;

@DatabaseTable(name = "student")
public class Students {

    @TextColumn(isIndexed = true, isUnique = true)
    public static final String NAME = "name";

    @TextColumn
    public static final String IMAGE_URI = "imageUri";

    @ForeignIdColumn(foreignTable = Words.class, foreignColumn = DatabaseTable.ID)
    public static final String STARTING_WORD = "startingWord";

    @ForeignIdColumn(foreignTable = Words.class, foreignColumn = DatabaseTable.ID)
    public static final String ENDING_WORD = "endingWord";

    public interface Student extends DatabaseObject {
        public static final String IMAGE_UNCHANGED = "imageUnchanged";

        @ColumnLink(NAME) String getName();
        @ColumnLink(IMAGE_URI) String getImageUri();
        @ColumnLink(STARTING_WORD) long getStartingWord();
        @ColumnLink(ENDING_WORD) long getEndingWord();
    }

    public interface Model {
        @SingleItemRetriever
        @Columns({DatabaseTable.ID, NAME, IMAGE_URI, STARTING_WORD, ENDING_WORD})
        @Select(DatabaseTable.ID + " = ?")
        Observable<Student> getStudent(@SelectArg long studentId);

        @MultipleItemRetriever
        @Columns({DatabaseTable.ID, NAME, IMAGE_URI})
        Observable<ArrayList<Student>> getStudents();

        @ItemUpdater
        @IncludeColumnIf(column = IMAGE_URI, notValue = Student.IMAGE_UNCHANGED)
        void updateOrInsert(Student student);
    }


}
