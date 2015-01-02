package com.frederis.notsureifreading.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.frederis.notsureifreading.ForApplication;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.screen.RecentAssessmentListScreen;
import com.frederis.notsureifreading.screen.StudentsListScreen;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import flow.Flow;
import rx.Observable;
import rx.Subscriber;
import rx.subjects.BehaviorSubject;

@Singleton
public class NavigationDrawer {

    private final Context mContext;
    private final SharedPreferences mSharedPreferences;

    @Inject
    public NavigationDrawer(@ForApplication Context context, SharedPreferences sharedPreferences) {
        mContext = context;
        mSharedPreferences = sharedPreferences;
    }

    public Observable<ArrayList<NavigationDrawerItem>> getItems() {
        BehaviorSubject<ArrayList<NavigationDrawerItem>> items = BehaviorSubject.create(new ArrayList<NavigationDrawerItem>());

        getNavigationItemList().subscribe(items);

        return items;
    }


    private Observable<ArrayList<NavigationDrawerItem>> getNavigationItemList() {
        return Observable.create(new Observable.OnSubscribe<ArrayList<NavigationDrawerItem>>() {
            @Override
            public void call(Subscriber<? super ArrayList<NavigationDrawerItem>> subscriber) {
               subscriber.onNext(getNavigationItems());
            }
        });
    }

    private ArrayList<NavigationDrawerItem> getNavigationItems() {
        return new ArrayList<NavigationDrawerItem>() {
            {
                add(new NavigationDrawerItem() {
                    @Override
                    public int getTextResId() {
                        return R.string.students;
                    }

                    @Override
                    public Object getTransitionScreen() {
                        return new StudentsListScreen();
                    }

                });

                add(new NavigationDrawerItem() {
                    @Override
                    public int getTextResId() {
                        return R.string.assessments;
                    }

                    @Override
                    public Object getTransitionScreen() {
                        return new RecentAssessmentListScreen();
                    }

                });

                add(new NavigationDrawerItem() {
                    @Override
                    public int getTextResId() {
                        return R.string.log;
                    }

                    @Override
                    public Object getTransitionScreen() {
                        return new StudentsListScreen();
                    }

                });
            }
        };
    }

}
