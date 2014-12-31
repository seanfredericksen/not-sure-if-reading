package com.frederis.notsureifreading.animation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Accepts 4 Animator resource ids corresponding:
 * <ol>
 * <li> View to be added</li>
 * <li> View to be removed</li>
 * <li>  View to be restored when popping the backstack</li>
 * <li> View to be popped from the backstack</li>
 * </ol>
 *
 * If no animation should be run, use an empty animator:  {@link Transitions#NONE}
 */
@Retention(RUNTIME) @Target(TYPE)
public @interface Transition {
    int[] value();
}
