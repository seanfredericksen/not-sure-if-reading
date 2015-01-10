package com.frederis.notsureifreading.database.Ideas;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseTable {
    String name();
    int createdVersion() default 1;
    int deletedVersion() default 0;

    public static final String ID = "_id";
}
