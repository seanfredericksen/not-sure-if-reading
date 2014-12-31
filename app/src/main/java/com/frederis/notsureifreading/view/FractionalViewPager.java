package com.frederis.notsureifreading.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class FractionalViewPager extends ViewPager {

    private float yFraction;
    private float xFraction;

    private int screenHeight;
    private int screenWidth;

    public FractionalViewPager(Context context) {
        super(context);
    }

    public FractionalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
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
