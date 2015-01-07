package com.frederis.notsureifreading.model;

import java.util.ArrayList;

import rx.Observable;

public interface AssessmentModel {
    Observable<ArrayList<Assessment>> getAllAssessments();
    Observable<Assessment> getAssessment(long assessmentId);
    void updateOrInsert(Assessment assessment);
}
