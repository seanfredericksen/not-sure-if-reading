package com.frederis.notsureifreading.database.Ideas;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Column(type = Column.Type.STRING)
public @interface TextColumn {
    int createVersion() default 1;
    int deleteVersion() default 0;
    boolean isUnique() default false;
    boolean isIndexed() default false;
    String defaultValue() default "";
}
