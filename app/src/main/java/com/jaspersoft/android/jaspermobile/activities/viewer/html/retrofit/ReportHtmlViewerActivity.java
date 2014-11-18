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

package com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboSpiceFragmentActivity;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.FilterManagerFragment;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.FilterManagerFragment_;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.PaginationManagerFragment;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.PaginationManagerFragment_;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.ReportActionFragment;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.ReportActionFragment_;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.ReportExecutionFragment;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.ReportExecutionFragment_;
import com.jaspersoft.android.jaspermobile.util.ScrollableTitleHelper;
import com.jaspersoft.android.jaspermobile.util.ServerInfoHolder;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.client.oxm.server.ServerInfo;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.api.ViewServer;

/**
 * Activity that performs report viewing in HTML format.
 *
 * @author Ivan Gadzhega
 * @author Tom Koptel
 * @since 1.4
 */
@EActivity(R.layout.report_viewer_layout)
public class ReportHtmlViewerActivity extends RoboSpiceFragmentActivity {

    @Extra
    ResourceLookup resource;
    @Bean
    ScrollableTitleHelper scrollableTitleHelper;

    @Inject
    JsRestClient jsRestClient;
    @Inject
    ServerInfoHolder infoHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            scrollableTitleHelper.injectTitle(this, resource.getLabel());
        }

        if (savedInstanceState == null) {
            commitFragments();
        }
    }

    private void commitFragments() {
        ServerInfo serverInfo = infoHolder.getServerInfo();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        PaginationManagerFragment paginationManagerFragment = PaginationManagerFragment_
                .builder().versionCode(serverInfo.getVersionCode()).build();
        transaction.add(R.id.control, paginationManagerFragment, PaginationManagerFragment.TAG);

        ReportExecutionFragment reportExecutionFragment = ReportExecutionFragment_.builder()
                .resource(resource).versionCode(serverInfo.getVersionCode()).build();
        transaction.add(reportExecutionFragment, ReportExecutionFragment.TAG);

        ReportActionFragment reportActionFragment = ReportActionFragment_.builder()
                .resource(resource).build();
        transaction.add(reportActionFragment, ReportActionFragment.TAG);

        FilterManagerFragment filterManagerFragment = FilterManagerFragment_.builder()
                .resource(resource).build();
        transaction.add(filterManagerFragment, FilterManagerFragment.TAG);

        transaction.commit();
    }

    @OptionsItem(android.R.id.home)
    final void goBack() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewServer.get(this).addWindow(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
    }

}