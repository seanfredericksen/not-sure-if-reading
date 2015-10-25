package com.frederis.notsureifreading.model;

public class RecentAssessment {

    public Assessment assessment;
    public String studentName;
    public int totalCorrect;
    public int totalPossible;
    public int percentAccuracy;

    public RecentAssessment(Assessment assessment,
                            String studentName,
                            int totalCorrect,
                            int totalPossible,
                            int percentAccuracy) {
        this.assessment = assessment;
        this.studentName = studentName;
        this.totalCorrect = totalCorrect;
        this.totalPossible = totalPossible;
        this.percentAccuracy = percentAccuracy;
    }

}
