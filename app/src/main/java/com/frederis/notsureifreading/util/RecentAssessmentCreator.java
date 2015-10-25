package com.frederis.notsureifreading.util;

import com.frederis.notsureifreading.model.Assessment;
import com.frederis.notsureifreading.model.RecentAssessment;
import com.frederis.notsureifreading.model.Students;

import javax.inject.Inject;

import rx.functions.Func1;

public class RecentAssessmentCreator implements Func1<Assessment, RecentAssessment> {

    private Students mStudents;

    @Inject
    public RecentAssessmentCreator(Students students) {
        mStudents = students;
    }

    @Override
    public RecentAssessment call(Assessment assessment) {
        int totalWords = (int) (assessment.getEndingWord() - assessment.getStartingWord() + 1);
        int totalCorrect = getTotalCorrect(assessment, totalWords);

        return new RecentAssessment(assessment,
                mStudents.readStudent(assessment.getStudentId()).getName(),
                totalCorrect,
                totalWords,
                Math.round(((float) totalCorrect / (float) totalWords) * 100f));
    }

    private int getTotalCorrect(Assessment assessment, int totalWords) {
        int correct = 0;

        for (int i = 0; i < totalWords; i++) {
            long result =
                    (i < 50
                            ? assessment.getOneToFiftyResult()
                            : assessment.getFiftyOneToOneHundredResult()
                    ) & (1L << (i < 50 ? (49 - i) : (99 - i)));

            if (result != 0L) {
                correct++;
            }
        }

        return correct;
    }

}
