/*
 * Copyright (C) 2012-2014 Jaspersoft Corporation. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.activities.viewer.html;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.async.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.activities.report.ReportOptionsActivity;
import com.jaspersoft.android.jaspermobile.activities.report.SaveReportActivity;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboSpiceFragmentActivity;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.fragment.WebViewFragment;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.fragment.WebViewFragment_;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.async.request.RunReportExecutionRequest;
import com.jaspersoft.android.sdk.client.async.request.cacheable.GetInputControlsRequest;
import com.jaspersoft.android.sdk.client.oxm.control.InputControl;
import com.jaspersoft.android.sdk.client.oxm.control.InputControlsList;
import com.jaspersoft.android.sdk.client.oxm.report.ReportExecutionResponse;
import com.jaspersoft.android.sdk.client.oxm.report.ReportParameter;
import com.jaspersoft.android.sdk.util.FileUtils;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity that performs report viewing in HTML format.
 *
 * @author Ivan Gadzhega
 * @author Tom Koptel
 * @since 1.4
 */
@EActivity
@OptionsMenu(R.menu.report_menu)
public class ReportHtmlViewerActivity extends RoboSpiceFragmentActivity
        implements WebViewFragment.OnWebViewCreated {

    // Extras
    public static final String EXTRA_REPORT_PARAMETERS = "ReportHtmlViewerActivity.EXTRA_REPORT_PARAMETERS";
    // Result Code
    private static final int REQUEST_REPORT_PARAMETERS = 100;
    private static final String OUTPUT_FORMAT = "HTML";

    @Inject
    JsRestClient jsRestClient;

    @Extra
    String resourceUri;
    @Extra
    String resourceLabel;

    @OptionsMenuItem
    MenuItem saveReport;
    @OptionsMenuItem
    MenuItem favoriteAction;
    @OptionsMenuItem
    MenuItem showFilters;

    @InstanceState
    ArrayList<InputControl> cachedInputControls;

    private WebViewFragment webViewFragment;
    private ArrayList<ReportParameter> reportParameters;
    private boolean mSaveActionVisible, mFilterActionVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            webViewFragment = WebViewFragment_.builder()
                    .resourceLabel(resourceLabel).resourceUri(resourceUri).build();
            webViewFragment.setOnWebViewCreated(this);
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, webViewFragment, WebViewFragment.TAG)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        saveReport.setVisible(mSaveActionVisible);
        showFilters.setVisible(mFilterActionVisible);
        return result;
    }

    @OptionsItem
    final void showFilters() {
        showReportOptions(cachedInputControls);
    }

    @OptionsItem
    final void saveReport() {
        if (!FileUtils.isExternalStorageWritable()) {
            // storage not available
            Toast.makeText(ReportHtmlViewerActivity.this, R.string.rv_t_external_storage_not_available, Toast.LENGTH_SHORT).show();
        } else {
            // save report
            Intent saveReport = new Intent();
            saveReport.setClass(this, SaveReportActivity.class);
            saveReport.putExtra(WebViewFragment.EXTRA_RESOURCE_URI, resourceUri);
            saveReport.putExtra(WebViewFragment.EXTRA_RESOURCE_LABEL, resourceLabel);
            saveReport.putExtra(EXTRA_REPORT_PARAMETERS, reportParameters);
            startActivity(saveReport);
        }
    }

    @OnActivityResult(REQUEST_REPORT_PARAMETERS)
    final void loadReportParameters(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            reportParameters = data.getParcelableArrayListExtra(EXTRA_REPORT_PARAMETERS);
            final RunReportExecutionRequest request = new RunReportExecutionRequest(jsRestClient,
                    resourceUri, OUTPUT_FORMAT, reportParameters);

            ProgressDialogFragment.show(getSupportFragmentManager(),
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            if (!request.isCancelled()) {
                                getSpiceManager().cancel(request);
                            }
                        }
                    },
                    new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            getSpiceManager().execute(request, new RunReportExecutionListener());
                        }
                    });
        } else {
            finish();
        }
    }

    //---------------------------------------------------------------------
    // Implements WebViewFragment.OnWebViewCreated
    //---------------------------------------------------------------------

    @Override
    public void onWebViewCreated(WebViewFragment webViewFragment) {
        final GetInputControlsRequest request =
                new GetInputControlsRequest(jsRestClient, resourceUri);

        ProgressDialogFragment.show(getSupportFragmentManager(),
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (!request.isCancelled()) {
                            getSpiceManager().cancel(request);
                            finish();
                        }
                    }
                },
                new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        getSpiceManager().execute(request,
                                new GetInputControlsListener());
                    }
                });
    }

    private void loadUrl(String reportUrl) {
        if (webViewFragment != null) {
            webViewFragment.loadUrl(reportUrl);
        }
    }

    private void showReportOptions(ArrayList<InputControl> inputControls) {
        // Run Report Options activity
        Intent intent = new Intent(this, ReportOptionsActivity.class);
        intent.putExtra(ReportOptionsActivity.EXTRA_REPORT_URI, resourceUri);
        intent.putExtra(ReportOptionsActivity.EXTRA_REPORT_LABEL, resourceLabel);
        intent.putParcelableArrayListExtra(ReportOptionsActivity.EXTRA_REPORT_CONTROLS, inputControls);
        startActivityForResult(intent, REQUEST_REPORT_PARAMETERS);
    }

    //---------------------------------------------------------------------
    // Inner class
    //---------------------------------------------------------------------

    private class GetInputControlsListener implements RequestListener<InputControlsList> {

        @Override
        public void onRequestFailure(SpiceException exception) {
            if (exception instanceof RequestCancelledException) {
                Toast.makeText(ReportHtmlViewerActivity.this,
                        R.string.cancelled_msg, Toast.LENGTH_SHORT).show();
            } else {
                RequestExceptionHandler.handle(exception, ReportHtmlViewerActivity.this, false);
            }
            ProgressDialogFragment.dismiss(getSupportFragmentManager());
        }

        @Override
        public void onRequestSuccess(InputControlsList controlsList) {
            ProgressDialogFragment.dismiss(getSupportFragmentManager());

            ArrayList<InputControl> inputControls = new ArrayList<InputControl>(controlsList.getInputControls());
            mFilterActionVisible = !inputControls.isEmpty();
            invalidateOptionsMenu();

            if (mFilterActionVisible) {
                cachedInputControls = inputControls;
                showReportOptions(inputControls);
            } else {
                List<ReportParameter> reportParameters = Lists.newArrayList();
                String reportUrl = jsRestClient.generateReportUrl(resourceUri, reportParameters, OUTPUT_FORMAT);
                loadUrl(reportUrl);
            }
        }
    }

    private class RunReportExecutionListener implements RequestListener<ReportExecutionResponse> {
        @Override
        public void onRequestFailure(SpiceException exception) {
            RequestExceptionHandler.handle(exception, ReportHtmlViewerActivity.this, true);
            ProgressDialogFragment.dismiss(getSupportFragmentManager());
        }

        @Override
        public void onRequestSuccess(ReportExecutionResponse response) {
            ProgressDialogFragment.dismiss(getSupportFragmentManager());

            String executionId = response.getRequestId();
            String exportOutput = response.getExports().get(0).getId();
            URI reportUri = jsRestClient.getExportOuptutResourceURI(executionId, exportOutput);
            loadUrl(reportUri.toString());
        }
    }

}