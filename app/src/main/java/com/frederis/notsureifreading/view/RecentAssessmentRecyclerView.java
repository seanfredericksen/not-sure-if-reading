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
import com.frederis.notsureifreading.model.Assessment;
import com.frederis.notsureifreading.util.SubscriptionUtil;
import com.frederis.notsureifreading.widget.BezelImageView;

import java.util.ArrayList;

import rx.Observable;

public class RecentAssessmentRecyclerView extends RecyclerView
        implements SubscriptionUtil.ListDataHandler<ArrayList<Assessment>> {

    private OnAssessmentSelectedListener mListener;

    public RecentAssessmentRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setHasFixedSize(true);
        setLayoutManager(new LinearLayoutManager(context));
        addItemDecoration(new DividerItemDecoration(context, null, true, true));
    }

    public void showAssessments(Observable<ArrayList<Assessment>> assessments) {
        SubscriptionUtil.subscribeListView(assessments, this);
    }

    public void setOnAssessmentSelectedListener(OnAssessmentSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public void setData(ArrayList<Assessment> data) {
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
        private ArrayList<Assessment> mAssessments;
        private OnAssessmentSelectedListener mListener;

        public Adapter(Context context, ArrayList<Assessment> assessments, OnAssessmentSelectedListener listener) {
            mContext = context;
            mAssessments = assessments;
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_view_student_list, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final Assessment assessment = mAssessments.get(position);

            viewHolder.studentName.setText("Assessment for student: " + assessment.getStudentId());
            viewHolder.row.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onAssessmentSelected(assessment.getId());
                }
            });

        }

        @Override
        public long getItemId(int position) {
            return mAssessments.get(position).getId();
        }

        @Override
        public int getItemCount() {
            return mAssessments.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public View row;
            public TextView studentName;
            public TextView wordsDescription;
            public BezelImageView studentImage;

            public ViewHolder(View view) {
                super(view);

                row = view;
                studentName = (TextView) view.findViewById(R.id.student_name);
                wordsDescription = (TextView) view.findViewById(R.id.sight_words_desc);
                studentImage = (BezelImageView) view.findViewById(R.id.student_image);
            }
        }

    }

    public static interface OnAssessmentSelectedListener {
        void onAssessmentSelected(long id);
    }

}
