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
import com.frederis.notsureifreading.model.Log;
import com.frederis.notsureifreading.screen.LogScreen;
import com.frederis.notsureifreading.util.SubscriptionUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import javax.inject.Inject;

import mortar.Mortar;
import rx.Observable;
import rx.Subscription;

public class LogRecyclerView extends RecyclerView
        implements SubscriptionUtil.ListDataHandler<ArrayList<Log>> {

    @Inject LogScreen.Presenter presenter;

    private Subscription mSubscription;

    public LogRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setHasFixedSize(true);
        setLayoutManager(new LinearLayoutManager(context));
        addItemDecoration(new DividerItemDecoration(context, null, true, true));

        Mortar.inject(context, this);
    }

    public void showLogs(Observable<ArrayList<Log>> logs) {
        mSubscription = SubscriptionUtil.subscribeListView(logs, this);
    }

    @Override
    public void setListData(ArrayList<Log> data) {
        setAdapter(new Adapter(data));
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        presenter.takeView(this);
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }

        presenter.dropView(this);
    }

    private static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private ArrayList<Log> mLogs;
        private SimpleDateFormat mDateFormat;

        public Adapter(ArrayList<Log> logs) {
            mLogs = logs;
            mDateFormat = new SimpleDateFormat("cccc, MMM d");
            mDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_view_log, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final Log log = mLogs.get(position);

            viewHolder.logMessage.setText(log.getMessage());
            viewHolder.logDate.setText(mDateFormat.format(log.getDate()));
        }

        @Override
        public long getItemId(int position) {
            return mLogs.get(position).getId();
        }

        @Override
        public int getItemCount() {
            return mLogs.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public TextView logMessage;
            public TextView logDate;

            public ViewHolder(View view) {
                super(view);

                logMessage = (TextView) view.findViewById(R.id.log_message);
                logDate = (TextView) view.findViewById(R.id.log_date);
            }
        }

    }
}
