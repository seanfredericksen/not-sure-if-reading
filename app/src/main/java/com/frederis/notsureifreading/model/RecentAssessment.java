package com.frederis.notsureifreading.model;

public class RecentAssessment {

    public Assessment assessment;
    public String studentName;
    public int percentAccuracy;

    public RecentAssessment(Assessment assessment, String studentName, int percentAccuracy) {
        this.assessment = assessment;
        this.studentName = studentName;
        this.percentAccuracy = percentAccuracy;
    }

}
