/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from TIBCO Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of TIBCO Jaspersoft Mobile for Android.
 *
 * TIBCO Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TIBCO Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TIBCO Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.ui.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;
import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.BuildConfig;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.SecurityProviderUpdater;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;

import org.androidannotations.api.ViewServer;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * @author Andrew Tivodar
 * @author Tom Koptel
 * @since 2.0
 */
public class ToolbarActivity extends BaseActivity {

    private static final String TAG = ToolbarActivity.class.getSimpleName();
    private static final int SECURITY_PROVIDER_DIALOG_REQUEST_CODE = 1123;
    private static final String CLOSE_APP_REQUEST_CODE = "close_app";

    private Toolbar toolbar;
    private FrameLayout toolbarCustomView;
    private View baseView;
    private ViewGroup contentLayout;

    private boolean mSecureProviderDialogShown;
    private boolean windowToolbar;

    @Inject
    protected SecurityProviderUpdater mSecurityProviderUpdater;
    @Inject
    protected Analytics analytics;
    @Inject
    protected JasperServer mServer;

    private StartupDelegate mStartupDelegate;

    public boolean isDevMode() {
        return BuildConfig.DEBUG && BuildConfig.FLAVOR.equals("dev");
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * Set custom view instead of toolbar title. Can be used only after activity is created.
     *
     * @param view custom view for Toolbar. Pass null to show default toolbar.
     */
    public void setCustomToolbarView(View view) {
        if (toolbarCustomView == null) return;

        toolbarCustomView.removeAllViews();
        getSupportActionBar().setDisplayShowTitleEnabled(view == null);
        if (view != null) {
            toolbarCustomView.addView(view);
        }
    }

    /**
     * Set whether a custom toolbar view should be displayed, if set.
     * If false, action bar title will be shown.
     */
    public void setDisplayCustomToolbarEnable(boolean enabled) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(toolbarCustomView.getChildCount() == 0 || !enabled);
        }

        if (toolbarCustomView != null) {
            for (int i = 0; i < toolbarCustomView.getChildCount(); i++) {
                toolbarCustomView.getChildAt(i).setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.tag(TAG);
        closeAppIfNeed();
        if (isDevMode()) {
            ViewServer.get(this).addWindow(this);
        }

        getBaseActivityComponent().inject(this);

        super.onCreate(savedInstanceState);
        mStartupDelegate = new StartupDelegate(this);
        mStartupDelegate.onCreate(savedInstanceState);

        setScreenName();
        addToolbar();
    }

    private void closeAppIfNeed() {
        if (getIntent().getBooleanExtra(CLOSE_APP_REQUEST_CODE, false)) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mStartupDelegate.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SECURITY_PROVIDER_DIALOG_REQUEST_CODE) {
            // Adding a fragment via GoogleApiAvailability.showErrorDialogFragment
            // before the instance state is restored throws an error. So instead,
            // set a flag here, which will cause the fragment to delay until
            // onPostResume.
            mSecureProviderDialogShown = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStartupDelegate.onResume();

        if (isDevMode()) {
            ViewServer.get(this).setFocusedWindow(this);
        }
        trackScreenView();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // We can now safely retry Security provider installation.
        if (mSecureProviderDialogShown) {
            mSecurityProviderUpdater.update(this, new ProviderInstallListener());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStartupDelegate.onDestroy();

        if (isDevMode()) {
            ViewServer.get(this).removeWindow(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mStartupDelegate.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void setContentView(View view) {
        if (!windowToolbar) {
            super.setContentView(view);
            return;
        }

        contentLayout.removeAllViews();
        contentLayout.addView(view);
        super.setContentView(baseView);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (!windowToolbar) {
            super.setContentView(layoutResID);
            return;
        }

        contentLayout.removeAllViews();
        LayoutInflater.from(this).inflate(layoutResID, contentLayout, true);
        super.setContentView(baseView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void addToolbar() {
        TypedArray a = getTheme().obtainStyledAttributes(new int[]{R.attr.windowToolbar});
        windowToolbar = a.getBoolean(0, true);
        if (!windowToolbar) return;

        LayoutInflater li = LayoutInflater.from(this);
        baseView = li.inflate(R.layout.view_base_toolbox_layout, null, false);
        contentLayout = (ViewGroup) baseView.findViewById(R.id.content);
        toolbar = (Toolbar) baseView.findViewById(R.id.tb_navigation);
        toolbarCustomView = (FrameLayout) toolbar.findViewById(R.id.tb_custom);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        super.setContentView(baseView);
    }

    protected String getScreenName() {
        return null;
    }

    protected void setScreenName() {
        String screenName = getScreenName();
        if (screenName != null) {
            analytics.setScreenName(screenName);
        }
    }

    protected void trackScreenView() {
        String screenName = getScreenName();
        if (screenName != null) {
            analytics.sendScreenView(screenName, null);
        }
    }

    private class ProviderInstallListener implements ProviderInstaller.ProviderInstallListener {
        @Override
        public void onProviderInstalled() {
            // Provider is up-to-date, app can make secure network calls.
        }

        @Override
        public void onProviderInstallFailed(int errorCode, Intent intent) {
            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
            if (googleApiAvailability.isUserResolvableError(errorCode) && !mSecureProviderDialogShown) {
                // Recoverable error. Show a dialog prompting the user to
                // install/update/enable Google Play services.
                googleApiAvailability.showErrorDialogFragment(
                        ToolbarActivity.this,
                        errorCode,
                        SECURITY_PROVIDER_DIALOG_REQUEST_CODE,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                // The user chose not to take the recovery action
                                closeApp();
                            }
                        });
            } else {
                // Google Play services is not available.
                closeApp();
            }
        }

        private void closeApp() {
            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(CLOSE_APP_REQUEST_CODE, true);
            startActivity(intent);
        }
    }
}
