package com.frederis.notsureifreading.database.Ideas;

public @interface Database {
    String name();
    int version();
    Class[] tables();
}
