package com.frederis.notsureifreading.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.model.AssessmentAnswer;
import com.frederis.notsureifreading.util.LUtils;
import com.frederis.notsureifreading.util.SubscriptionUtil;
import com.frederis.notsureifreading.widget.CheckableFrameLayout;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;

public class AssessmentWordsRecyclerView extends RecyclerView
        implements SubscriptionUtil.ListDataHandler<ArrayList<AssessmentAnswer>> {

    public AssessmentWordsRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setHasFixedSize(true);
        setLayoutManager(new GridLayoutManager(context, 2));
    }

    public Subscription showAssessmentAnswers(Observable<ArrayList<AssessmentAnswer>> assessmentAnswers) {
        return SubscriptionUtil.subscribeListView(assessmentAnswers, this);
    }

    @Override
    public void setListData(ArrayList<AssessmentAnswer> data) {
        setAdapter(new Adapter(getContext(), data));
    }

    private static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private ArrayList<AssessmentAnswer> mAnswers;
        private LUtils mLUtils;

        public Adapter(Context context, ArrayList<AssessmentAnswer> answers) {
            mAnswers = answers;
            mLUtils = new LUtils(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.assessment_review_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final AssessmentAnswer answer = mAnswers.get(position);

            viewHolder.wordName.setText(answer.getWord().getText());
            viewHolder.wordIdentifiedButton.setChecked(answer.isCorrect(), false);
            mLUtils.setOrAnimateWordIdentifiedIcon(viewHolder.wordIdentifiedIcon, answer.isCorrect(), false);
        }

        @Override
        public long getItemId(int position) {
            return mAnswers.get(position).getWord().getId();
        }

        @Override
        public int getItemCount() {
            return mAnswers.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public TextView wordName;
            public CheckableFrameLayout wordIdentifiedButton;
            public ImageView wordIdentifiedIcon;

            public ViewHolder(View view) {
                super(view);

                wordName = (TextView) view.findViewById(R.id.assessment_review_word_name);
                wordIdentifiedButton = (CheckableFrameLayout) view.findViewById(R.id.assessment_review_button);
                wordIdentifiedIcon = (ImageView) wordIdentifiedButton.findViewById(R.id.assessment_review_icon);
            }
        }

    }

}
