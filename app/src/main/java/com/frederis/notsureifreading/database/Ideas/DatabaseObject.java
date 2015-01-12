package com.frederis.notsureifreading.database.Ideas;

public interface DatabaseObject {
    @ColumnLink(DatabaseTable.ID) long getId();
}
