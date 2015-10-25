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
            int totalWords = (int) (assessment.getEndingWord() - assessment.getStartingWord() + 1);
            int totalCorrect = getTotalCorrect(assessment, totalWords);

            recentAssessments.add(new RecentAssessment(assessment,
                    mStudents.readStudent(assessment.getStudentId()).getName(),
                    totalCorrect,
                    totalWords,
                    Math.round(((float) totalCorrect / (float) totalWords) * 100f)));
        }

        return recentAssessments;
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
