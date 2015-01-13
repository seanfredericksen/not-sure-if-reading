package com.frederis.notsureifreading.model;

import com.frederis.notsureifreading.database.Ideas.Assessments;

public class RecentAssessment {

    public Assessments.Assessment assessment;
    public String studentName;
    public int percentAccuracy;

    public RecentAssessment(Assessments.Assessment assessment, String studentName, int percentAccuracy) {
        this.assessment = assessment;
        this.studentName = studentName;
        this.percentAccuracy = percentAccuracy;
    }

}
