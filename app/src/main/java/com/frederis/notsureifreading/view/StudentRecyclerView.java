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
import com.frederis.notsureifreading.model.Student;
import com.frederis.notsureifreading.util.SubscriptionUtil;
import com.frederis.notsureifreading.widget.BezelImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import rx.Observable;

public class StudentRecyclerView extends RecyclerView
        implements SubscriptionUtil.ListDataHandler<ArrayList<Student>> {

    private OnStudentSelectedListener mListener;

    public StudentRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setHasFixedSize(true);
        setLayoutManager(new LinearLayoutManager(context));
        addItemDecoration(new DividerItemDecoration(context, null, true, true));
    }

    public void showStudents(Observable<ArrayList<Student>> students) {
        SubscriptionUtil.subscribeListView(students, this);
    }

    private static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private Context mContext;
        private ArrayList<Student> mStudents;
        private OnStudentSelectedListener mListener;

        public Adapter(Context context, ArrayList<Student> students, OnStudentSelectedListener listener) {
            mContext = context;
            mStudents = students;
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_view_student_list, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final Student student = mStudents.get(position);

            viewHolder.studentName.setText(student.getName());
            viewHolder.wordsDescription.setText(mContext.getString(R.string.words_description, student.getStartingWord(), student.getEndingWord()));
            viewHolder.row.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onStudentSelected(student.getId());
                }
            });

            Picasso.with(mContext)
                    .load(student.getImageUri())
                    .placeholder(R.drawable.contact_picture_placeholder)
                    .fit()
                    .centerCrop()
                    .tag(mContext)
                    .into(viewHolder.studentImage);
        }

        @Override
        public long getItemId(int position) {
            return mStudents.get(position).getId();
        }

        @Override
        public int getItemCount() {
            return mStudents.size();
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

    public void setOnStudentSelectedListener(OnStudentSelectedListener listener) {
        mListener = listener;
    }

    public static interface OnStudentSelectedListener {
        void onStudentSelected(long id);
    }

    @Override
    public void setListData(ArrayList<Student> data) {
        setAdapter(new Adapter(getContext(), data, new OnStudentSelectedListener() {
            @Override
            public void onStudentSelected(long id) {
                if (mListener != null) {
                    mListener.onStudentSelected(id);
                }
            }
        }));

    }

}
