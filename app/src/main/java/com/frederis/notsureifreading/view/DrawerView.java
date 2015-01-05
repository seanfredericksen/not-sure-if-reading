package com.frederis.notsureifreading.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.decoration.DividerItemDecoration;
import com.frederis.notsureifreading.model.NavigationDrawerItem;
import com.frederis.notsureifreading.screen.DrawerScreen;
import com.frederis.notsureifreading.util.SubscriptionUtil;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import mortar.Mortar;
import rx.Observable;

public class DrawerView extends RecyclerView implements SubscriptionUtil.ListDataHandler<ArrayList<NavigationDrawerItem>> {

    @Inject DrawerScreen.Presenter presenter;

    private int mCurrentSelectedPosition = 0;

    public DrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Mortar.inject(context, this);

        setHasFixedSize(true);
        setLayoutManager(new LinearLayoutManager(context));
        addItemDecoration(new DividerItemDecoration(context.getResources().getDrawable(R.drawable.divider)));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);

        presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    public void showDrawerItems(Observable<ArrayList<NavigationDrawerItem>> items) {
        SubscriptionUtil.subscribeListView(items, this);
    }

    private static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private Context mContext;
        private ArrayList<NavigationDrawerItem> mItems;
        private OnNavigationDrawerItemSelectedListener mListener;

        public Adapter(Context context, ArrayList<NavigationDrawerItem> items, OnNavigationDrawerItemSelectedListener listener) {
            mContext = context;
            mItems = items;
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_view_navigation_drawer, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final NavigationDrawerItem item = mItems.get(position);

            holder.text.setText(item.getTextResId());
            holder.icon.setImageResource(item.getIconResId());
            holder.row.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onNavigationDrawerItemSelected(item);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public View row;
            public ImageView icon;
            public TextView text;

            public ViewHolder(View view) {
                super(view);

                row = view;
                icon = (ImageView) view.findViewById(R.id.item_icon);
                text = (TextView) view.findViewById(R.id.item_text);
            }
        }
    }

    @Override
    public void setData(ArrayList<NavigationDrawerItem> data) {
        setAdapter(new Adapter(getContext(), data, new OnNavigationDrawerItemSelectedListener() {
            @Override
            public void onNavigationDrawerItemSelected(NavigationDrawerItem item) {
                presenter.onDrawerItemSelected(item);
            }
        }));
    }

    public static interface OnNavigationDrawerItemSelectedListener {
        void onNavigationDrawerItemSelected(NavigationDrawerItem item);
    }

}