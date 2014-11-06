package com.frederis.notsureifreading.actionbar;

import android.content.Context;
import android.os.Bundle;
import mortar.Mortar;
import mortar.MortarScope;
import mortar.Presenter;
import rx.functions.Action0;

public class ActionBarOwner extends Presenter<ActionBarOwner.View> {

    public static class Config {
        public final boolean showHomeEnabled;
        public final boolean upButtonEnabled;
        public final CharSequence title;
        public final MenuAction action;

        public Config(boolean showHomeEnabled, boolean upButtonEnabled, CharSequence title,
                      MenuAction action) {
            this.showHomeEnabled = showHomeEnabled;
            this.upButtonEnabled = upButtonEnabled;
            this.title = title;
            this.action = action;
        }

        public Config withAction(MenuAction action) {
            return new Config(showHomeEnabled, upButtonEnabled, title, action);
        }
    }

    public static class MenuAction {
        public final CharSequence title;
        public final Action0 action;

        public MenuAction(CharSequence title, Action0 action) {
            this.title = title;
            this.action = action;
        }
    }

    private Config config;

    ActionBarOwner() {
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
        view.setTitle(config.title);
        view.setMenu(config.action);
    }

    public interface View {
        void setShowHomeEnabled(boolean enabled);

        void setUpButtonEnabled(boolean enabled);

        void setTitle(CharSequence title);

        void setMenu(MenuAction action);

        Context getMortarContext();
    }

}