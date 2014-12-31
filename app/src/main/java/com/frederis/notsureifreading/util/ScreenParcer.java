package com.frederis.notsureifreading.util;

import android.os.Parcelable;

import flow.Parcer;

public final class ScreenParcer<T> implements Parcer<T> {

    @Override
    public Parcelable wrap(T instance) {
        return (Parcelable) instance;
    }

    @Override
    public T unwrap(Parcelable parcelable) {
        //noinspection unchecked
        return (T) parcelable;
    }

}