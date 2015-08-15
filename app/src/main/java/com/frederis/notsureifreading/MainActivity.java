package com.frederis.notsureifreading;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.frederis.notsureifreading.actionbar.ToolbarOwner;
import com.frederis.notsureifreading.presenter.ActivityResultPresenter;
import com.frederis.notsureifreading.view.CoreView;

import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import mortar.Mortar;
import mortar.MortarActivityScope;
import mortar.MortarScope;
import timber.log.Timber;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_LAUNCHER;

public class MainActivity extends ActionBarActivity implements ToolbarOwner.View, ActivityResultPresenter.View {

    private MortarActivityScope activityScope;
    private ToolbarOwner.MenuActions actionBarMenuActions;
    private Toolbar mToolbar;

    @Inject ToolbarOwner toolbarOwner;
    @Inject ActivityResultPresenter activityResultPresenter;

    private CoreView coreView;
    private Flow mainFlow;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isWrongInstance()) {
            finish();
            return;
        }

        MortarScope parentScope = Mortar.getScope(getApplication());
        activityScope = Mortar.requireActivityScope(parentScope, new CoreBlueprint());
        activityScope.onCreate(savedInstanceState);

        Mortar.inject(this, this);


        setContentView(R.layout.activity_main);
        coreView = (CoreView) findViewById(R.id.core);
        mainFlow = coreView.getFlow();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        toolbarOwner.takeView(this);
        activityResultPresenter.takeView(this);
    }

    @Override public Object getSystemService(String name) {
        if (Mortar.isScopeSystemService(name)) {
            return activityScope;
        }
        return super.getSystemService(name);
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        activityScope.onSaveInstanceState(outState);
    }

    /** Inform the view about back events. */
    @Override public void onBackPressed() {
        // Give the view a chance to handle going back. If it declines the honor, let super do its thing.
        if (!mainFlow.goBack()) super.onBackPressed();
    }

    /** Inform the view about up events. */
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return mainFlow.goUp() || mainFlow.goBack();
        }

        return actionBarMenuActions != null && actionBarMenuActions.callback.onMenuItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /** Configure the action bar menu as required by {@link com.frederis.notsureifreading.actionbar.ToolbarOwner.View}. */
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        if (actionBarMenuActions != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(actionBarMenuActions.menuResource, menu);

            actionBarMenuActions.callback.onConfigureOptionsMenu(menu);
        }

        return true;
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        toolbarOwner.dropView(this);
        activityResultPresenter.dropView(this);

        // activityScope may be null in case isWrongInstance() returned true in onCreate()
        if (isFinishing() && activityScope != null) {
            MortarScope parentScope = Mortar.getScope(getApplication());
            parentScope.destroyChild(activityScope);
            activityScope = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override public Context getMortarContext() {
        return this;
    }

    @Override public void setShowHomeEnabled(boolean enabled) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(enabled);
    }

    @Override public void setUpButtonEnabled(boolean enabled) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(enabled);
        actionBar.setHomeButtonEnabled(enabled);
    }

    @Override public void setTitleResId(int titleResId) {
        getSupportActionBar().setTitle(titleResId);
    }

    @Override public void setMenu(ToolbarOwner.MenuActions action) {
        if (action != actionBarMenuActions) {
            actionBarMenuActions = action;
            invalidateOptionsMenu();
        }
    }

    @Override
    public void setElevationDimension(int elevationDimensionResId) {
        ViewCompat.setElevation(mToolbar, getResources().getDimension(elevationDimensionResId));
    }

    /**
     * Dev tools and the play store (and others?) launch with a different intent, and so
     * lead to a redundant instance of this activity being spawned. <a
     * href="http://stackoverflow.com/questions/17702202/find-out-whether-the-current-activity-will-be-task-root-eventually-after-pendin"
     * >Details</a>.
     */
    private boolean isWrongInstance() {
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            boolean isMainAction = intent.getAction() != null && intent.getAction().equals(ACTION_MAIN);
            return intent.hasCategory(CATEGORY_LAUNCHER) && isMainAction;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        activityResultPresenter.onActivityResultReceived(requestCode, resultCode, data);
    }

    @Override
    public MortarScope getMortarScope() {
        return activityScope;
    }

    @Override public void startActivity(Intent intent) {
        if (canHandleIntent(intent)) {
            startActivity(intent);
        } else {
            Timber.e("Could not handle intent %s... ignoring", intent);
        }
    }

    @Override public void startActivityForResult(int requestCode, Intent intent) {
        if (canHandleIntent(intent)) {
            startActivityForResult(intent, requestCode);
        } else {
            Timber.e("Could not handle intent %s... ignoring", intent);
        }
    }

    private boolean canHandleIntent(Intent intent) {
        PackageManager manager = getPackageManager();
        List<ResolveInfo> info = manager.queryIntentActivities(intent, 0);
        return info.size() > 0;
    }

}