package com.frederis.notsureifreading.database.Ideas;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    Type type();

    public enum Type {
        STRING, INT, FOREIGN_ID
    }
}
