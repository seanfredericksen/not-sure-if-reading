package com.frederis.notsureifreading.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class FractionalLinearLayout extends LinearLayout {

    private float yFraction;
    private float xFraction;

    private int screenHeight;
    private int screenWidth;

    public FractionalLinearLayout(Context context) {
        super(context);
    }

    public FractionalLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FractionalLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FractionalLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenHeight = h;
        screenWidth = w;
    }

    public void setYFraction(float fraction) {
        yFraction = fraction;
        setY(screenHeight > 0 ? (yFraction * screenHeight) : 0);
    }

    public float getYFraction() {
        return yFraction;
    }

    public void setXFraction(float fraction) {
        xFraction = fraction;
        setX(screenWidth > 0 ? (xFraction * screenWidth) : 0);
    }

    public float getXFraction() {
        return xFraction;
    }
}