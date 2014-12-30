package com.frederis.notsureifreading;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.frederis.notsureifreading.actionbar.ToolbarOwner;
import com.frederis.notsureifreading.presenter.ActivityResultPresenter;
import com.frederis.notsureifreading.presenter.ActivityResultProvider;
import com.frederis.notsureifreading.util.StartActivityForResultHandler;
import com.frederis.notsureifreading.view.MainView;

import javax.inject.Inject;

import flow.Flow;
import mortar.Mortar;
import mortar.MortarActivityScope;
import mortar.MortarScope;
import mortar.MortarScopeDevHelper;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_LAUNCHER;
import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;

public class MainActivity extends ActionBarActivity implements ToolbarOwner.View, StartActivityForResultHandler, ActivityResultProvider {

    static final String IMAGE_CAPTURE_URI = "imageCaptureUri";
    static final String IMAGE_CAPTURE_STUDENT_ID = "imageCaptureStudentId";

    private MortarActivityScope activityScope;
    private ToolbarOwner.MenuAction actionBarMenuAction;
    private Toolbar mToolbar;

    @Inject ToolbarOwner toolbarOwner;
    @Inject ActivityResultPresenter activityResultPresenter;

    private Flow mainFlow;

    private Uri mImageCaptureUri;
    private long mImageCaptureStudentId;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isWrongInstance()) {
            finish();
            return;
        }

        MortarScope parentScope = Mortar.getScope(getApplication());
        activityScope = Mortar.requireActivityScope(parentScope, new MainBlueprint(this));
        Mortar.inject(this, this);

        activityScope.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mImageCaptureUri = savedInstanceState.getParcelable(IMAGE_CAPTURE_URI);
            mImageCaptureStudentId = savedInstanceState.getLong(IMAGE_CAPTURE_STUDENT_ID);
        }

        setContentView(R.layout.activity_main);
        MainView mainView = (MainView) findViewById(R.id.container);
        mainFlow = mainView.getFlow();

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

        outState.putParcelable(IMAGE_CAPTURE_URI, mImageCaptureUri);
        outState.putLong(IMAGE_CAPTURE_STUDENT_ID, mImageCaptureStudentId);
    }

    /** Inform the view about back events. */
    @Override public void onBackPressed() {
        // Give the view a chance to handle going back. If it declines the honor, let super do its thing.
        if (!mainFlow.goBack()) super.onBackPressed();
    }

    /** Inform the view about up events. */
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return mainFlow.goUp();
        }

        return super.onOptionsItemSelected(item);
    }

    /** Configure the action bar menu as required by {@link com.frederis.notsureifreading.actionbar.ToolbarOwner.View}. */
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        if (actionBarMenuAction != null) {
            menu.add(actionBarMenuAction.title)
                    .setShowAsActionFlags(SHOW_AS_ACTION_ALWAYS)
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override public boolean onMenuItemClick(MenuItem menuItem) {
                            actionBarMenuAction.action.call();
                            return true;
                        }
                    });
        }
        menu.add("Log Scope Hierarchy")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.d("DemoActivity", MortarScopeDevHelper.scopeHierarchyToString(activityScope));
                        return true;
                    }
                });
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

    @Override public Context getMortarContext() {
        return this;
    }

    @Override public void setShowHomeEnabled(boolean enabled) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
    }

    @Override public void setUpButtonEnabled(boolean enabled) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(enabled);
        actionBar.setHomeButtonEnabled(enabled);
    }

    @Override public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override public void setMenu(ToolbarOwner.MenuAction action) {
        if (action != actionBarMenuAction) {
            actionBarMenuAction = action;
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
        if (!activityResultPresenter.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    @Override
    public MortarScope getMortarScope() {
        return activityScope;
    }
}