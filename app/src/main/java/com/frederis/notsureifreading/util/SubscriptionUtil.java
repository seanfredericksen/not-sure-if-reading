package com.frederis.notsureifreading.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.frederis.notsureifreading.R;
import com.squareup.picasso.Picasso;

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

    static public <T extends Uri> Subscription subscribeStudentImage(final Observable<T> observable, final Context context, final ImageView imageView) {
        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<T>() {
                    @Override
                    public void call(T data) {
                        Picasso.with(context)
                                .load(data)
                                .placeholder(R.drawable.contact_picture_placeholder)
                                .fit()
                                .centerCrop()
                                .tag(context)
                                .into(imageView);
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
                        dataHandler.setListData(data);
                    }
                });
    }

    static public <T> Subscription subscribeViewPager(final Observable<T> observable,
                                                      final PagerDataHandler<T> dataHandler) {
        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<T>() {
                    @Override
                    public void call(T data) {
                        dataHandler.setData(data);
                    }
                });
    }

    public interface ImageDataHandler<T> {
        void setData(T data);
    }

    public interface ListDataHandler<T> {
        void setListData(T data);
    }

    public interface PagerDataHandler<T> {
        void setData(T data);
    }


}
