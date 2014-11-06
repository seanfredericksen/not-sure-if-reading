package com.frederis.notsureifreading.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.frederis.notsureifreading.R;

import flow.Flow;
import flow.Layouts;
import mortar.Blueprint;
import mortar.Mortar;
import mortar.MortarScope;

import static android.view.animation.AnimationUtils.loadAnimation;

/**
 * A conductor that can swap subviews within a container view.
 * <p/>
 *
 * @param <S> the type of the screens that serve as a {@link Blueprint} for subview. Must
 *            be annotated with {@link flow.Layout}, suitable for use with {@link flow.Layouts#createView}.
 */
public class ScreenConductor<S extends Blueprint> implements CanShowScreen<S> {

    private final Context context;
    private final ViewGroup container;

    /**
     * @param container the container used to host child views. Typically this is a {@link
     *                  android.widget.FrameLayout} under the action bar.
     */
    public ScreenConductor(Context context, ViewGroup container) {
        this.context = context;
        this.container = container;
    }

    public void showScreen(S screen, Flow.Direction direction) {
        MortarScope myScope = Mortar.getScope(context);
        MortarScope newChildScope = myScope.requireChild(screen);

        View oldChild = getChildView();
        View newChild;

        if (oldChild != null) {
            MortarScope oldChildScope = Mortar.getScope(oldChild.getContext());
            if (oldChildScope.getName().equals(screen.getMortarScopeName())) {
                // If it's already showing, short circuit.
                return;
            }

            myScope.destroyChild(oldChildScope);
        }

        // Create the new child.
        Context childContext = newChildScope.createContext(context);
        newChild = Layouts.createView(childContext, screen);

        setAnimation(direction, oldChild, newChild);

        // Out with the old, in with the new.
        if (oldChild != null) {
            container.removeView(oldChild);
        }

        container.addView(newChild);
    }

    protected void setAnimation(Flow.Direction direction, View oldChild, View newChild) {
        if (oldChild == null) return;

        int out = direction == Flow.Direction.FORWARD ? R.anim.slide_out_left : R.anim.slide_out_right;
        int in = direction == Flow.Direction.FORWARD ? R.anim.slide_in_right : R.anim.slide_in_left;

        oldChild.setAnimation(loadAnimation(context, out));
        newChild.setAnimation(loadAnimation(context, in));
    }

    private View getChildView() {
        return container.getChildAt(0);
    }
}