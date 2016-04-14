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

package com.jaspersoft.android.jaspermobile.activities.viewer.html.dashboard;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.util.ScrollableTitleHelper;
import com.jaspersoft.android.jaspermobile.webview.WebInterface;
import com.jaspersoft.android.jaspermobile.webview.WebViewEnvironment;
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.AmberDashboardExecutor;
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.DashboardCallback;
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.DashboardExecutor;
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.DashboardWebInterface;
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.JsDashboardTrigger;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.UiThread;

/**
 * @author Tom Koptel
 * @since 2.0
 */
@EActivity
public class AmberDashboardActivity extends BaseDashboardActivity implements DashboardCallback {

    @InstanceState
    protected boolean mMaximized;

    @InstanceState
    protected boolean mPaused;

    private Toast mToast;
    private int mOrientation;
    private boolean mFavoriteItemVisible, mRefreshItemVisible, mInfoItemVisible;
    private MenuItem favoriteAction, refreshAction, aboutAction;
    private JsDashboardTrigger mDashboardTrigger;
    private DashboardExecutor mDashboardExecutor;
    private WebInterface webInterface;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        showMenuItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        favoriteAction = menu.findItem(R.id.favoriteAction);
        refreshAction = menu.findItem(R.id.refreshAction);
        aboutAction = menu.findItem(R.id.aboutAction);
        return result;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean result = super.onPrepareOptionsMenu(menu);
        favoriteAction.setVisible(mFavoriteItemVisible);
        refreshAction.setVisible(mRefreshItemVisible);
        aboutAction.setVisible(mInfoItemVisible);
        return result;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation != mOrientation && mOrientation != -1 && !mPaused) {
            mOrientation = newConfig.orientation;
            showLoading();
        }
    }

    @Override
    protected void onPause() {
        mPaused = true;
        if (webInterface != null) {
            webInterface.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
        if (webInterface != null) {
            webInterface.resume();
        }
    }

    @Override
    protected String getScreenName() {
        return getString(R.string.ja_dvs_hn);
    }

    //---------------------------------------------------------------------
    // Abstract methods implementations
    //---------------------------------------------------------------------

    @Override
    public void onWebViewConfigured(WebView webView) {
        mDashboardTrigger = JsDashboardTrigger.with(webView);
        mDashboardExecutor = AmberDashboardExecutor.newInstance(webView, mServer, resource);
        webInterface = DashboardWebInterface.from(this);
        WebViewEnvironment
                .configure(webView)
                .withWebInterface(webInterface);
        showInitialLoader();
        loadFlow();
    }

    @Override
    public void onPageFinished() {
        webView.loadUrl(mScriptTagFactory.getTagCreator(resource).createTag());
    }

    @Override
    public void onRefresh() {
        loadFlow();
    }

    @Override
    public void onHomeAsUpCalled() {
        if (mMaximized && webView != null) {
            mDashboardTrigger.minimizeDashlet();
            scrollableTitleHelper.injectTitle(resource.getLabel());
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSessionRefreshed() {
        showInitialLoader();
        loadFlow();
    }

    //---------------------------------------------------------------------
    // DashboardCallback implementations
    //---------------------------------------------------------------------

    @UiThread
    @Override
    public void onMaximizeStart(String title) {
        resetZoom();
        hideMenuItems();
        showLoading();
    }

    @UiThread
    @Override
    public void onMaximizeEnd(String title) {
        ProgressDialogFragment.dismiss(getSupportFragmentManager());
        mMaximized = true;
        scrollableTitleHelper.injectTitle(title);
    }

    @UiThread
    @Override
    public void onMaximizeFailed(String error) {
        hideLoading();
    }

    @UiThread
    @Override
    public void onMinimizeStart() {
        resetZoom();
        showMenuItems();
        showLoading();
    }

    @UiThread
    @Override
    public void onMinimizeEnd() {
        hideLoading();
        mMaximized = false;
    }

    @UiThread
    @Override
    public void onMinimizeFailed(String error) {
        hideLoading();
    }

    @UiThread
    @Override
    public void onScriptLoaded() {
        runDashboard();
    }

    @UiThread
    @Override
    public void onLoadStart() {
    }

    @UiThread
    @Override
    public void onLoadDone() {
        hideInitialLoader();
    }

    @UiThread
    @Override
    public void onLoadError(String error) {
        hideLoading();
        mToast.setText(error);
        mToast.show();
    }

    @UiThread
    @Override
    public void onReportExecution(String data) {
    }

    @UiThread
    @Override
    public void onWindowResizeStart() {
    }

    @UiThread
    @Override
    public void onWindowResizeEnd() {
        hideLoading();
    }

    @Override
    public void onAuthError(String message) {
    }

    @UiThread
    @Override
    public void onWindowError(String errorMessage) {
        showMessage(getString(R.string.failed_load_data));
        hideLoading();
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void loadFlow() {
        mDashboardExecutor.prepare();
    }

    private void runDashboard() {
        mDashboardExecutor.execute();
    }

    private void showInitialLoader() {
        webView.setVisibility(View.INVISIBLE);
        showLoading();
    }

    private void hideInitialLoader() {
        webView.setVisibility(View.VISIBLE);
        hideLoading();
    }

    private void showMenuItems() {
        mFavoriteItemVisible = mRefreshItemVisible = mInfoItemVisible = true;
        supportInvalidateOptionsMenu();
    }

    private void hideMenuItems() {
        mFavoriteItemVisible = mRefreshItemVisible = mInfoItemVisible = false;
        supportInvalidateOptionsMenu();
    }
}
