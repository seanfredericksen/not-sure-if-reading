package com.frederis.notsureifreading.util;

import com.frederis.notsureifreading.model.Assessment;
import com.frederis.notsureifreading.model.RecentAssessment;
import com.frederis.notsureifreading.model.Students;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.functions.Func1;

public class RecentAssessmentsCreator implements Func1<ArrayList<Assessment>, ArrayList<RecentAssessment>> {

    private Students mStudents;

    @Inject
    public RecentAssessmentsCreator(Students students) {
        mStudents = students;
    }

    @Override
    public ArrayList<RecentAssessment> call(ArrayList<Assessment> assessments) {
        ArrayList<RecentAssessment> recentAssessments = new ArrayList<>();

        for (Assessment assessment : assessments) {
            recentAssessments.add(new RecentAssessment(assessment,
                    mStudents.readStudent(assessment.getStudentId()).getName(),
                    calculateAccuracy(assessment)));
        }

        return recentAssessments;
    }

    private int calculateAccuracy(Assessment assessment) {
        float total = (float) (assessment.getEndingWord() - assessment.getStartingWord() + 1);
        float correct = 0.0f;

        for (int i = 0; i < total; i++) {
            long result =
                    (i < 50
                            ? assessment.getOneToFiftyResult()
                            : assessment.getFiftyOneToOneHundredResult()
                    ) & (1L << (i < 50 ? (49 - i) : (99 - i)));

            if (result != 0L) {
                correct++;
            }
        }

        return Math.round((correct / total) * 100f);
    }

}
