package com.frederis.notsureifreading.database.Ideas;

public interface DatabaseObject {
    @ColumnLink("_id") long getId();
}
