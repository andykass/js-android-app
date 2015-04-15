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

import android.os.Bundle;
import android.webkit.WebView;

import com.jaspersoft.android.jaspermobile.activities.viewer.html.dashboard.webview.flow.WebFlowFactory;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.dashboard.webview.script.ScriptTagFactory;
import com.jaspersoft.android.jaspermobile.util.ScrollableTitleHelper;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * Activity that performs dashboard viewing in HTML format.
 *
 * @author Ivan Gadzhega
 * @since 1.4
 */
@EActivity
public class LegacyDashboardViewerActivity extends DashboardCordovaActivity {

    @Bean
    protected ScrollableTitleHelper scrollableTitleHelper;
    @Extra
    protected ResourceLookup resource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scrollableTitleHelper.injectTitle(resource.getLabel());
        loadFlow();
    }

    @Override
    public void setupWebView(WebView webView) {
    }

    @Override
    public void onPageFinished() {
        webView.loadUrl("javascript:" + ScriptTagFactory.getInstance(this).getTagCreator().createTag());
    }

    @Override
    public void onRefresh() {
        loadFlow();
    }

    @Override
    public void onHomeAsUpCalled() {
        super.onBackPressed();
    }

    private void loadFlow() {
        WebFlowFactory.getInstance(this).createFlow(resource).load(webView);
    }

}