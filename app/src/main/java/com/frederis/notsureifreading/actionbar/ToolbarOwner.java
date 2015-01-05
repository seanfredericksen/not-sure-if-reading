package com.frederis.notsureifreading.actionbar;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import mortar.Mortar;
import mortar.MortarScope;
import mortar.Presenter;

public class ToolbarOwner extends Presenter<ToolbarOwner.View> {

    public static class Config {
        public final boolean showHomeEnabled;
        public final boolean upButtonEnabled;
        public final int titleResId;
        public final MenuActions actions;
        public final int elevationDimensionResId;

        public Config(boolean showHomeEnabled, boolean upButtonEnabled, int titleResId,
                      MenuActions actions, int elevationDimensionResId) {
            this.showHomeEnabled = showHomeEnabled;
            this.upButtonEnabled = upButtonEnabled;
            this.titleResId = titleResId;
            this.actions = actions;
            this.elevationDimensionResId = elevationDimensionResId;
        }

        public static class Builder {

            public boolean showHomeEnabled;
            public boolean upButtonEnabled;
            public int titleResId;
            public MenuActions actions;
            public int elevationDimensionResId;

            public Builder withShowHomeEnabled(boolean enabled) {
                showHomeEnabled = enabled;
                return this;
            }

            public Builder withUpEnabled(boolean enabled) {
                upButtonEnabled = enabled;
                return this;
            }

            public Builder withTitleResId(int titleResId) {
                this.titleResId = titleResId;
                return this;
            }

            public Builder withActions(MenuActions actions) {
                this.actions = actions;
                return this;
            }

            public Builder withElevationDimensionResId(int elevationDimensionResId) {
                this.elevationDimensionResId = elevationDimensionResId;
                return this;
            }

            public Config build() {
                return new Config(showHomeEnabled, upButtonEnabled, titleResId, actions, elevationDimensionResId);
            }
        }

    }

    public static class MenuActions {
        public final int menuResource;
        public final Callback callback;

        public MenuActions(int menuResource, Callback callback) {
            this.menuResource = menuResource;
            this.callback = callback;
        }

        public static interface Callback {
            void onConfigureOptionsMenu(Menu menu);
            boolean onMenuItemSelected(MenuItem menuItem);
        }
    }

    private Config config;

    public ToolbarOwner() {
    }

    @Override
    public void onLoad(Bundle savedInstanceState) {
        if (config != null) {
            update();
        }
    }

    public void setConfig(Config config) {
        this.config = config;
        update();
    }

    public Config getConfig() {
        return config;
    }

    @Override
    protected MortarScope extractScope(View view) {
        return Mortar.getScope(view.getMortarContext());
    }

    private void update() {
        View view = getView();
        if (view == null) return;

        view.setShowHomeEnabled(config.showHomeEnabled);
        view.setUpButtonEnabled(config.upButtonEnabled);
        view.setTitleResId(config.titleResId);
        view.setMenu(config.actions);
        view.setElevationDimension(config.elevationDimensionResId);
    }

    public interface View {
        void setShowHomeEnabled(boolean enabled);
        void setUpButtonEnabled(boolean enabled);
        void setTitleResId(int titleResId);
        void setMenu(MenuActions action);
        void setElevationDimension(int elevationDimensionResId);
        Context getMortarContext();
    }

}