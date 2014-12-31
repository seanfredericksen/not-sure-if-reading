package com.frederis.notsureifreading.view;


import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.frederis.notsureifreading.CoreBlueprint;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.util.CanShowDrawer;
import com.frederis.notsureifreading.util.CanShowScreen;
import com.frederis.notsureifreading.util.ScreenConductor;

import javax.inject.Inject;

import flow.Flow;
import mortar.Blueprint;
import mortar.Mortar;

public class CoreView extends DrawerLayout implements CanShowScreen<Blueprint>, CanShowDrawer<Blueprint> {

    @Inject CoreBlueprint.Presenter presenter;

    private final ScreenConductor<Blueprint> screenMaestro;
    private final Toolbar toolbar;
    private final FrameLayout contentContainer;
    private final FrameLayout navigationContainer;
    private ActionBarDrawerToggle drawerToggle;

    public CoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Mortar.inject(context, this);


        LayoutInflater.from(context).inflate(R.layout.core_view_root, this, true);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        contentContainer = (FrameLayout) findViewById(R.id.content_container);
        navigationContainer = (FrameLayout) findViewById(R.id.navigation_container);

        screenMaestro = new ScreenConductor<>(context, contentContainer, navigationContainer);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        presenter.takeView(this);

        initNavigationDrawer();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    public Flow getFlow() {
        return presenter.getFlow();
    }

    @Override
    public void showScreen(Blueprint screen, Blueprint oldScreen, Flow.Direction direction) {
        screenMaestro.showScreen(screen, oldScreen, direction);
    }

    @Override
    public void showDrawer(Blueprint screen) {
        screenMaestro.showDrawer(screen);
    }

    public void initNavigationDrawer() {
        drawerToggle = new ActionBarDrawerToggle(
                (Activity) getContext(),
                this,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });
        setDrawerListener(drawerToggle);

    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }

}