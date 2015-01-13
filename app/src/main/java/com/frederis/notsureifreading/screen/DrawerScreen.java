package com.frederis.notsureifreading.screen;

import android.os.Bundle;

import com.frederis.notsureifreading.CoreBlueprint;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.actionbar.DrawerPresenter;
import com.frederis.notsureifreading.model.NavigationDrawer;
import com.frederis.notsureifreading.model.NavigationDrawerItem;
import com.frederis.notsureifreading.view.DrawerView;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;
import rx.Observable;

@Layout(R.layout.drawer)
public class DrawerScreen implements Blueprint {
    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(
            injects = {
                    DrawerView.class
            },
            addsTo = CoreBlueprint.Module.class,
            library = true
    )
    public static class Module {

        @Provides
        Observable<ArrayList<NavigationDrawerItem>> provideDrawer(NavigationDrawer drawer) {
            return drawer.getItems();
        }

    }

    @Singleton
    public static class Presenter extends ViewPresenter<DrawerView> {

        private final Flow flow;
        private DrawerPresenter drawerPresenter;
        private final Observable<ArrayList<NavigationDrawerItem>> drawerItems;

        @Inject Presenter(Flow flow, DrawerPresenter drawerPresenter, Observable<ArrayList<NavigationDrawerItem>> drawerItems) {
            this.flow = flow;
            this.drawerPresenter = drawerPresenter;
            this.drawerItems = drawerItems;
        }

        @Override
        public void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            DrawerView view = getView();
            if (view == null) return;

            view.showDrawerItems(drawerItems);
        }

        public void onDrawerItemSelected(NavigationDrawerItem item) {
            drawerPresenter.closeDrawer();
            flow.replaceTo(item.getTransitionScreen());
        }

    }
}