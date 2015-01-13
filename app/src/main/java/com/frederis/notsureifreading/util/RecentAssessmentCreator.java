package com.frederis.notsureifreading.util;

import com.frederis.notsureifreading.model.RecentAssessment;
import com.frederis.notsureifreading.model.Students;

import javax.inject.Inject;

import rx.functions.Func1;

import static com.frederis.notsureifreading.database.Ideas.Assessments.Assessment;

public class RecentAssessmentCreator implements Func1<Assessment, RecentAssessment> {

    private Students mStudents;

    @Inject
    public RecentAssessmentCreator(Students students) {
        mStudents = students;
    }

    @Override
    public RecentAssessment call(Assessment assessment) {
        return new RecentAssessment(assessment,
                mStudents.readStudent(assessment.getStudentId()).getName(),
                calculateAccuracy(assessment));
    }

    private int calculateAccuracy(Assessment assessment) {
        float total = (float) (assessment.getEndingWord() - assessment.getStartingWord() + 1);
        float correct = 0.0f;

        for (int i = 0; i < total; i++) {
            long result =
                    (i < 50
                            ? assessment.getOneToFiftyResults()
                            : assessment.getFiftyOneToOneHundredResults()
                    ) & (1L << (i < 50 ? (49 - i) : (99 - i)));

            if (result != 0L) {
                correct++;
            }
        }

        return Math.round((correct / total) * 100f);
    }

}
