package com.frederis.notsureifreading.presenter;

import android.content.Intent;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import mortar.MortarScope;
import mortar.Presenter;
import mortar.Scoped;

public class ActivityResultPresenter extends Presenter<ActivityResultProvider>
        implements ActivityResultRegistrar {

    private final Set<Registration> registrations = new HashSet<>();

    @Inject
    public ActivityResultPresenter() {
    }

    @Override
    protected MortarScope extractScope(ActivityResultProvider view) {
        return view.getMortarScope();
    }

    @Override
    public void register(MortarScope scope, ActivityResultListener listener) {
        Registration registration = new Registration(listener);
        scope.register(registration);

        registrations.add(registration);
    }

    @Override
    protected void onExitScope() {
        super.onExitScope();

        registrations.clear();
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Registration registration : registrations) {
            if (registration.registrant.onActivityResult(requestCode, resultCode, data)) {
                return true;
            }
        }

        return false;
    }

    private class Registration implements Scoped {
        final ActivityResultListener registrant;

        private Registration(ActivityResultListener registrant) {
            this.registrant = registrant;
        }

        @Override public void onEnterScope(MortarScope scope) {
        }

        @Override public void onExitScope() {
            registrations.remove(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Registration that = (Registration) o;

            return registrant.equals(that.registrant);
        }

        @Override
        public int hashCode() {
            return registrant.hashCode();
        }
    }
}
