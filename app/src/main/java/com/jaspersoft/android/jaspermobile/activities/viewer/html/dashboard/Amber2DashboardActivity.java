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
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.InputControlsActivity;
import com.jaspersoft.android.jaspermobile.activities.inputcontrols.InputControlsActivity_;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.interactor.dashboard.GetDashboardControlsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.dashboard.GetDashboardVisualizeParamsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.FlushInputControlsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetReportMetadataCase;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.presentation.view.activity.ReportVisualizeActivity_;
import com.jaspersoft.android.jaspermobile.util.ScrollableTitleHelper;
import com.jaspersoft.android.jaspermobile.webview.WebInterface;
import com.jaspersoft.android.jaspermobile.webview.WebViewEnvironment;
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.AmberTwoDashboardExecutor;
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.DashboardCallback;
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.DashboardExecutor;
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.DashboardTrigger;
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.DashboardWebInterface;
import com.jaspersoft.android.jaspermobile.webview.dashboard.bridge.JsDashboardTrigger;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 2.0
 */
@OptionsMenu(R.menu.report_filter_manager_menu)
@EActivity
public class Amber2DashboardActivity extends BaseDashboardActivity implements DashboardCallback {

    private static final int REQUEST_DASHBOARDS_PARAMETERS = 200;

    @InstanceState
    protected boolean mMaximized;

    @Inject
    GetDashboardControlsCase mGetDashboardControlsCase;
    @Inject
    FlushInputControlsCase mFlushInputControlsCase;
    @Inject
    GetDashboardVisualizeParamsCase mGetDashboardVisualizeParamsCase;
    @Inject
    GetReportMetadataCase mGetReportMetadataCase;
    @Inject
    RequestExceptionHandler mExceptionHandler;

    private boolean mFavoriteItemVisible, mInfoItemVisible, mFiltersVisible;
    private MenuItem favoriteAction, aboutAction, filerAction;
    private DashboardTrigger mDashboardTrigger;
    private WebInterface mWebInterface;
    private DashboardExecutor mDashboardExecutor;

    private DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            Amber2DashboardActivity.super.onBackPressed();
        }
    };

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);

        mGetDashboardControlsCase.execute(resource.getUri(), new GenericSubscriber<>(new SimpleSubscriber<Boolean>() {
            @Override
            public void onError(Throwable e) {
                mFiltersVisible = false;
            }

            @Override
            public void onNext(Boolean hasControls) {
                mFiltersVisible = hasControls;
                invalidateOptionsMenu();
            }
        }));
        showMenuItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        favoriteAction = menu.findItem(R.id.favoriteAction);
        aboutAction = menu.findItem(R.id.aboutAction);
        filerAction = menu.findItem(R.id.showFilters);
        return result;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean result = super.onPrepareOptionsMenu(menu);
        favoriteAction.setVisible(mFavoriteItemVisible);
        aboutAction.setVisible(mInfoItemVisible);
        filerAction.setVisible(mFiltersVisible);

        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.showFilters) {
            InputControlsActivity_.intent(Amber2DashboardActivity.this)
                    .reportUri(resource.getUri())
                    .dashboardInputControl(true)
                    .startForResult(REQUEST_DASHBOARDS_PARAMETERS);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnActivityResult(REQUEST_DASHBOARDS_PARAMETERS)
    final void onNewParametersResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            boolean isNewParamsEqualOld = data.getBooleanExtra(
                    InputControlsActivity.RESULT_SAME_PARAMS, false);
            if (!isNewParamsEqualOld) {
                applyParams();
            }
        }
    }

    @Override
    protected void onPause() {
        if (mWebInterface != null) {
            mWebInterface.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebInterface != null) {
            mWebInterface.resume();
        }
    }

    @Override
    protected String getScreenName() {
        return getString(R.string.ja_dvs_v);
    }

    //---------------------------------------------------------------------
    // Abstract methods implementations
    //---------------------------------------------------------------------

    @Override
    public void onWebViewConfigured(WebView webView) {
        mDashboardTrigger = JsDashboardTrigger.with(webView);
        mDashboardExecutor = AmberTwoDashboardExecutor.newInstance(webView, mServer, resource);
        mWebInterface = DashboardWebInterface.from(this);
        WebViewEnvironment.configure(webView)
                .withWebInterface(mWebInterface);
        loadFlow();
    }

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
        hideLoading();
        resetZoom();
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
    }

    @UiThread
    @Override
    public void onScriptLoaded() {
        runDashboard();
    }

    @UiThread
    @Override
    public void onLoadStart() {
        showLoading();
    }

    @UiThread
    @Override
    public void onLoadDone() {
        showWebView(true);
        hideLoading();
    }

    @UiThread
    @Override
    public void onLoadError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        hideLoading();
    }

    @UiThread
    @Override
    public void onReportExecution(String data) {
        mGetReportMetadataCase.execute(data, new GenericSubscriber<>(new SimpleSubscriber<ResourceLookup>() {
            @Override
            public void onNext(ResourceLookup lookup) {
                ReportVisualizeActivity_.intent(Amber2DashboardActivity.this)
                        .resource(lookup)
                        .start();
            }
        }));
    }

    @Override
    public void onWindowResizeStart() {
    }

    @Override
    public void onWindowResizeEnd() {
    }

    @UiThread
    @Override
    public void onAuthError(String message) {
        scrollableTitleHelper.injectTitle(resource.getLabel());
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        super.onSessionExpired();
    }

    @UiThread
    @Override
    public void onWindowError(String errorMessage) {
        showMessage(getString(R.string.failed_load_data));
        hideLoading();
    }

    @Override
    public void onPageFinished() {
    }

    @Override
    public void onRefresh() {
        if (mMaximized) {
            mDashboardTrigger.refreshDashlet();
        } else {
            mDashboardTrigger.refreshDashboard();
        }
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
        loadFlow();
    }

    @Override
    public void finish() {
        mGetDashboardControlsCase.unsubscribe();
        mGetDashboardVisualizeParamsCase.unsubscribe();
        mGetReportMetadataCase.unsubscribe();
        mFlushInputControlsCase.execute(resource.getUri());
        super.finish();
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

    private void applyParams() {
        mGetDashboardVisualizeParamsCase.execute(resource.getUri(), new GenericSubscriber<>(new SimpleSubscriber<String>() {
            @Override
            public void onError(Throwable e) {
                String message = RequestExceptionHandler.extractMessage(Amber2DashboardActivity.this, e);
                showMessage(message);
            }

            @Override
            public void onNext(String params) {
                mDashboardTrigger.applyParams(params);
            }
        }));
    }

    private void showMenuItems() {
        mFavoriteItemVisible = mInfoItemVisible = true;
        supportInvalidateOptionsMenu();
    }

    private void hideMenuItems() {
        mFavoriteItemVisible = mInfoItemVisible = false;
        supportInvalidateOptionsMenu();
    }
}
