/*
 * Copyright © 2014 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Mobile for Android.
 *
 * Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Jaspersoft Mobile for Android. If not, see
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
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.AmberDashboardLoader;
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.DashboardCallback;
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.DashboardLoader;
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.DashboardWebInterface;
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.JsDashboardTrigger;
import com.jaspersoft.android.jaspermobile.webview.dashboard.script.ScriptTagFactory;
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

    @Bean
    protected ScrollableTitleHelper scrollableTitleHelper;

    @Extra
    protected ResourceLookup resource;

    @InstanceState
    protected boolean mMaximized;

    @InstanceState
    protected boolean mPaused;

    private Toast mToast;
    private int mOrientation;
    private boolean mFavoriteItemVisible, mRefreshItemVisible, mInfoItemVisible;
    private MenuItem favoriteAction, refreshAction, aboutAction;
    private JsDashboardTrigger mDashboardTrigger;
    private DashboardLoader mDashboardLoader;

    private DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener(){
        @Override
        public void onCancel(DialogInterface dialog) {
            AmberDashboardActivity.super.onBackPressed();
        }
    };
    private WebInterface webInterface;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        scrollableTitleHelper.injectTitle(resource.getLabel());
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
            ProgressDialogFragment.builder(getSupportFragmentManager())
                    .setLoadingMessage(R.string.loading_msg)
                    .show();
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

    //---------------------------------------------------------------------
    // Abstract methods implementations
    //---------------------------------------------------------------------

    @Override
    public void onWebViewConfigured(WebView webView) {
        mDashboardTrigger = JsDashboardTrigger.with(webView);
        mDashboardLoader = AmberDashboardLoader.newInstance(webView, resource);
        webInterface = DashboardWebInterface.from(this);
        WebViewEnvironment
                .configure(webView)
                .withWebInterface(webInterface);
        showInitialLoader();
        loadFlow();
    }

    @Override
    public void onPageFinished() {
        webView.loadUrl(ScriptTagFactory.getInstance(this).getTagCreator(resource).createTag());
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
        ProgressDialogFragment.builder(getSupportFragmentManager())
                .setLoadingMessage(R.string.loading_msg)
                .show();
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
        ProgressDialogFragment.dismiss(getSupportFragmentManager());
    }

    @UiThread
    @Override
    public void onMinimizeStart() {
        resetZoom();
        showMenuItems();
        ProgressDialogFragment.builder(getSupportFragmentManager())
                .setLoadingMessage(R.string.loading_msg)
                .show();
    }

    @UiThread
    @Override
    public void onMinimizeEnd() {
        ProgressDialogFragment.dismiss(getSupportFragmentManager());
        mMaximized = false;
    }

    @UiThread
    @Override
    public void onMinimizeFailed(String error) {
        ProgressDialogFragment.dismiss(getSupportFragmentManager());
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
        ProgressDialogFragment.dismiss(getSupportFragmentManager());
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
        ProgressDialogFragment.dismiss(getSupportFragmentManager());
    }

    @Override
    public void onAuthError(String message) {
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void loadFlow() {
        mDashboardLoader.load();
    }

    private void runDashboard() {
        mDashboardLoader.run();
    }

    private void showInitialLoader() {
        webView.setVisibility(View.INVISIBLE);
        ProgressDialogFragment.builder(getSupportFragmentManager())
                .setLoadingMessage(R.string.da_loading)
                .setOnCancelListener(cancelListener)
                .show();
    }

    private void hideInitialLoader() {
        webView.setVisibility(View.VISIBLE);
        ProgressDialogFragment.dismiss(getSupportFragmentManager());
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
