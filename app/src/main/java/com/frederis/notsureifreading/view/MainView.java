package com.frederis.notsureifreading.view;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.frederis.notsureifreading.MainBlueprint;
import com.frederis.notsureifreading.util.CanShowScreen;
import com.frederis.notsureifreading.util.ScreenConductor;
import com.frederis.notsureifreading.util.TitledBlueprint;

import flow.Flow;

import javax.inject.Inject;

import mortar.Blueprint;
import mortar.Mortar;

public class MainView extends FrameLayout implements CanShowScreen<TitledBlueprint> {
    @Inject MainBlueprint.Presenter presenter;
    private final ScreenConductor<Blueprint> screenMaestro;

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Mortar.inject(context, this);
        screenMaestro = new ScreenConductor<Blueprint>(context, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        presenter.takeView(this);
    }

    public Flow getFlow() {
        return presenter.getFlow();
    }

    @Override
    public void showScreen(TitledBlueprint screen, Flow.Direction direction) {
        Log.d("NSIR", "maestro, take it away!");
        screenMaestro.showScreen(screen, direction);
    }
}