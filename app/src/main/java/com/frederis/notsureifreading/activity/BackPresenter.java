package com.frederis.notsureifreading.activity;

import javax.inject.Singleton;

import mortar.MortarScope;
import mortar.Presenter;

@Singleton
public class BackPresenter extends Presenter<BackPresenter.View> {

    @Override
    protected MortarScope extractScope(View view) {
        return view.getMortarScope();
    }

    public interface View {
        MortarScope getMortarScope();
        void setShouldPromptOnBack(boolean prompt);
    }

    public void setShouldPromptOnBack(boolean prompt) {
        if (getView() == null) return;

        getView().setShouldPromptOnBack(prompt);
    }

}
