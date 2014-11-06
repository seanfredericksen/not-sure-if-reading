package com.frederis.notsureifreading.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.frederis.notsureifreading.model.Assessment;
import com.frederis.notsureifreading.screen.RecentAssessmentListScreen;

import java.util.List;

import javax.inject.Inject;

import mortar.Mortar;

public class RecentAssessmentListView extends ListView {

    @Inject RecentAssessmentListScreen.Presenter presenter;

    public RecentAssessmentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Mortar.inject(context, this);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        presenter.takeView(this);
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    public void showAssessments(List<Assessment> assessments) {
        Adapter adapter = new Adapter(getContext(), assessments);

        setAdapter(adapter);
        setOnItemClickListener(new OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.onAssessmentSelected(position);
            }
        });
    }

    private static class Adapter extends ArrayAdapter<Assessment> {
        public Adapter(Context context, List<Assessment> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
        }
    }
}
