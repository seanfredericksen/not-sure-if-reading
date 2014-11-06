package com.frederis.notsureifreading.model;

import com.frederis.notsureifreading.database.NsirDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

@Singleton
public class Assessments {

    private long mCurrentId = 1L;

    private PublishSubject<Object> mUpdater = PublishSubject.create();

    @Inject
    public Assessments(NsirDatabase database) {

    }

    public void update() {
        mCurrentId = 3L;
        mUpdater.onNext(new Object());
    }

    public List<Assessment> getAll() {
        return new ArrayList<Assessment>() {
            {
                add(new Assessment(1L, "Assessment 1"));
                add(new Assessment(2L, "Assessment 2"));
            }
        };
    }

    public Observable<Assessment> getAssessment(long id) {
        BehaviorSubject<Assessment> assessment = BehaviorSubject.create(getAssessment());

        mUpdater.flatMap(new Func1<Object, Observable<Assessment>>() {
            @Override
            public Observable<Assessment> call(Object o) {
                return Observable.just(getAssessment());
            }
        }).subscribe(assessment);

        return assessment;
    }

    private Assessment getAssessment() {
        return new Assessment(mCurrentId, "Assessment: " + mCurrentId);
    }

}