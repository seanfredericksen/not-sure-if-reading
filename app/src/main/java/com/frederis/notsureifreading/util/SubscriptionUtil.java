package com.frederis.notsureifreading.util;

import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class SubscriptionUtil {

    static public Subscription subscribeTextViewText(final Observable<String> observable,
                                                     final TextView textView) {
        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        textView.setText(s);
                    }
                });
    }

    static public <T> Subscription subscribeListView(final Observable<T> observable,
                                                     final ListDataHandler<T> dataHandler) {
        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<T>() {
                    @Override
                    public void call(T data) {
                        dataHandler.setData(data);
                    }
                });
    }

    public static interface ListDataHandler<T> {
        void setData(T data);
    }


}
