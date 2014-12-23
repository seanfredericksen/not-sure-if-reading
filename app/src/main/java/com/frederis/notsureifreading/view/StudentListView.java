package com.frederis.notsureifreading.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.model.Student;
import com.frederis.notsureifreading.screen.StudentsListScreen;

import java.util.ArrayList;

import javax.inject.Inject;

import mortar.Mortar;
import rx.Observable;

public class StudentListView extends FrameLayout {

    @Inject StudentsListScreen.Presenter mPresenter;

    private StudentRecyclerView mRecycler;
    private View mAddStudent;

    public StudentListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize(context);
    }

    public StudentListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StudentListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initialize(context);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPresenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPresenter.dropView(this);
    }

    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.student_list_view_root, this, true);

        mRecycler = (StudentRecyclerView) findViewById(R.id.student_recycler_view);
        mAddStudent = findViewById(R.id.add_student_button);

        mAddStudent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onStudentSelected(0L);
            }
        });

        mRecycler.setOnStudentSelectedListener(new StudentRecyclerView.OnStudentSelectedListener() {
            @Override
            public void onStudentSelected(long id) {
                mPresenter.onStudentSelected(id);
            }
        });

        Mortar.inject(context, this);
    }

    public void showStudents(Observable<ArrayList<Student>> students) {
        mRecycler.showStudents(students);
    }

}
