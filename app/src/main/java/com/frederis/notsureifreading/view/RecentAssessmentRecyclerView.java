package com.frederis.notsureifreading.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.decoration.DividerItemDecoration;
import com.frederis.notsureifreading.model.RecentAssessment;
import com.frederis.notsureifreading.util.SubscriptionUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import rx.Observable;

public class RecentAssessmentRecyclerView extends RecyclerView
        implements SubscriptionUtil.ListDataHandler<ArrayList<RecentAssessment>> {

    private OnAssessmentSelectedListener mListener;

    public RecentAssessmentRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setHasFixedSize(true);
        setLayoutManager(new LinearLayoutManager(context));
        addItemDecoration(new DividerItemDecoration(context, null, true, true));
    }

    public void showAssessments(Observable<ArrayList<RecentAssessment>> assessments) {
        SubscriptionUtil.subscribeListView(assessments, this);
    }

    public void setOnAssessmentSelectedListener(OnAssessmentSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public void setData(ArrayList<RecentAssessment> data) {
        setAdapter(new Adapter(getContext(), data, new OnAssessmentSelectedListener() {
            @Override
            public void onAssessmentSelected(long id) {
                if (mListener != null) {
                    mListener.onAssessmentSelected(id);
                }
            }
        }));
    }

    private static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private Context mContext;
        private ArrayList<RecentAssessment> mAssessments;
        private OnAssessmentSelectedListener mListener;
        private SimpleDateFormat mDateFormat;

        public Adapter(Context context, ArrayList<RecentAssessment> assessments, OnAssessmentSelectedListener listener) {
            mContext = context;
            mAssessments = assessments;
            mListener = listener;
            mDateFormat = new SimpleDateFormat("cccc, MMM d");
            mDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_view_recent_assessment, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final RecentAssessment assessment = mAssessments.get(position);

            viewHolder.assessmentName.setText(assessment.studentName);
            viewHolder.assessmentDescription.setText(mContext.getString(R.string.assessment_description,
                    assessment.percentAccuracy + "%",
                    assessment.assessment.getStartingWord(),
                    assessment.assessment.getEndingWord()));
            viewHolder.assessmentDate.setText(mDateFormat.format(assessment.assessment.getDate()));
            viewHolder.row.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onAssessmentSelected(assessment.assessment.getId());
                }
            });

        }

        @Override
        public long getItemId(int position) {
            return mAssessments.get(position).assessment.getId();
        }

        @Override
        public int getItemCount() {
            return mAssessments.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public View row;
            public TextView assessmentName;
            public TextView assessmentDescription;
            public TextView assessmentDate;

            public ViewHolder(View view) {
                super(view);

                row = view;
                assessmentName = (TextView) view.findViewById(R.id.assessment_name);
                assessmentDescription = (TextView) view.findViewById(R.id.assessment_details);
                assessmentDate = (TextView) view.findViewById(R.id.assessment_date);
            }
        }

    }

    public static interface OnAssessmentSelectedListener {
        void onAssessmentSelected(long id);
    }

}
