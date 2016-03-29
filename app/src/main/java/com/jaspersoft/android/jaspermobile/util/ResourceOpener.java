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

package com.jaspersoft.android.jaspermobile.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.file.FileViewerActivity_;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.RepositoryControllerFragment;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.RepositoryControllerFragment_;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.RepositorySearchFragment;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.RepositorySearchFragment_;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.dashboard.Amber2DashboardActivity_;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.dashboard.AmberDashboardActivity_;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.dashboard.LegacyDashboardViewerActivity_;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.report.ReportCastActivity_;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ReportViewActivity_;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ReportVisualizeActivity_;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.ComponentProviderDelegate;
import com.jaspersoft.android.jaspermobile.util.cast.ResourcePresentationService;
import com.jaspersoft.android.jaspermobile.util.filtering.RepositoryResourceFilter_;
import com.jaspersoft.android.jaspermobile.util.filtering.ResourceFilter;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.service.data.server.ServerVersion;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import javax.inject.Inject;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EBean
public class ResourceOpener {

    @RootContext
    FragmentActivity activity;
    @Inject
    JasperServer mServer;

    ResourceFilter resourceFilter;
    private ServerVersion mServerVersion;
    private boolean mIsPro;

    @AfterInject
    final void init() {
        ComponentProviderDelegate.INSTANCE
                .getBaseActivityComponent(activity)
                .inject(this);
        resourceFilter = RepositoryResourceFilter_.getInstance_(activity);

        String version = mServer.getVersion();
        mServerVersion = ServerVersion.valueOf(version);
        mIsPro = mServer.isProEdition();
    }

    public void openResource(Fragment fragment, ResourceLookup resource) {
        openResource(fragment, RepositoryControllerFragment.PREF_TAG, resource);
    }

    public void openResource(Fragment fragment, String prefTag, ResourceLookup resource) {
        switch (resource.getResourceType()) {
            case folder:
                openFolder(fragment, prefTag, resource);
                break;
            case reportUnit:
                if (ResourcePresentationService.isStarted()) {
                    castReport(resource);
                } else {
                    runReport(resource);
                }
                break;
            case legacyDashboard:
            case dashboard:
                runDashboard(resource);
                break;
            case file:
                showFile(resource);
                break;
            default:
                showUnsupported();
                break;
        }
    }

    public void openFolder(Fragment fragment, String preftag, ResourceLookup resource) {
        RepositoryControllerFragment newControllerFragment =
                RepositoryControllerFragment_.builder()
                        .resourceLabel(resource.getLabel())
                        .resourceUri(resource.getUri())
                        .prefTag(preftag)
                        .build();
        FragmentTransaction transaction = fragment.getFragmentManager().beginTransaction();
        RepositorySearchFragment searchControllerFragment =
                RepositorySearchFragment_.builder()
                        .build();
        transaction
                .replace(R.id.search_controller, searchControllerFragment)
                .addToBackStack(resource.getUri())
                .replace(R.id.resource_controller, newControllerFragment)
                .commit();
    }

    private void runReport(final ResourceLookup resource) {
        boolean isRestEngine = mServerVersion.lessThan(ServerVersion.v6);
        boolean isCeJrs = !mIsPro;
        if (isCeJrs || isRestEngine) {
            ReportViewActivity_.intent(activity)
                    .resource(resource).start();
        } else {
            ReportVisualizeActivity_.intent(activity)
                    .resource(resource).start();
        }
    }

    private void castReport(final ResourceLookup resource) {
        ReportCastActivity_.intent(activity)
                .resource(resource).start();
    }

    private void runDashboard(ResourceLookup resource) {
        boolean isLegacyDashboard = (resource.getResourceType() == ResourceLookup.ResourceType.legacyDashboard);

        ServerVersion version = mServerVersion;
        boolean isLegacyEngine = version.lessThan(ServerVersion.v6);
        boolean isFlowEngine = version.greaterThanOrEquals(ServerVersion.v6) && version.lessThan(ServerVersion.v6_1);
        boolean isVisualizeEngine = version.greaterThanOrEquals(ServerVersion.v6_1);

        if (isLegacyDashboard || isLegacyEngine) {
            LegacyDashboardViewerActivity_.intent(activity).resource(resource).start();
            return;
        }

        if (isFlowEngine) {
            AmberDashboardActivity_.intent(activity).resource(resource).start();
            return;
        }

        if (isVisualizeEngine) {
            Amber2DashboardActivity_.intent(activity).resource(resource).start();
        }
    }

    private void showFile(ResourceLookup resource){
        FileViewerActivity_.intent(activity).resourceLookup(resource).start();
    }

    private void showUnsupported(){
        Toast.makeText(activity, R.string.fv_undefined_message, Toast.LENGTH_SHORT).show();
    }
}
