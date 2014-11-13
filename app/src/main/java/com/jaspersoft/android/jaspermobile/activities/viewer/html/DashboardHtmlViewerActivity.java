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

package com.jaspersoft.android.jaspermobile.activities.viewer.html;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.async.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboAccentFragmentActivity;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboSpiceFragmentActivity;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.emerald2.fragment.WebViewFragment;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.emerald2.fragment.WebViewFragment_;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.FilterManagerFragment;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.FilterManagerFragment_;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.PaginationManagerFragment;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.PaginationManagerFragment_;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.ReportActionFragment;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.ReportActionFragment_;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.ReportExecutionFragment;
import com.jaspersoft.android.jaspermobile.activities.viewer.html.retrofit.fragment.ReportExecutionFragment_;
import com.jaspersoft.android.jaspermobile.dialog.ProgressDialogFragment;
import com.jaspersoft.android.jaspermobile.util.FavoritesHelper;
import com.jaspersoft.android.sdk.client.JsRestClient;
import com.jaspersoft.android.sdk.client.async.request.cacheable.GetServerInfoRequest;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;
import com.jaspersoft.android.sdk.client.oxm.server.ServerInfo;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

/**
 * Activity that performs dashboard viewing in HTML format.
 *
 * @author Ivan Gadzhega
 * @since 1.4
 */
@EActivity
@OptionsMenu(R.menu.dashboard_menu)
public class DashboardHtmlViewerActivity extends RoboSpiceFragmentActivity
        implements WebViewFragment.OnWebViewCreated {

    @Inject
    JsRestClient jsRestClient;

    @OptionsMenuItem
    MenuItem favoriteAction;

    @Extra
    ResourceLookup resource;

    @Bean
    FavoritesHelper favoritesHelper;

    @InstanceState
    Uri favoriteEntryUri;

    private WebViewFragment webViewFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            favoriteEntryUri = favoritesHelper.queryFavoriteUri(resource);

            webViewFragment = WebViewFragment_.builder()
                    .resourceLabel(resource.getLabel()).resourceUri(resource.getUri()).build();
            webViewFragment.setOnWebViewCreated(this);
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, webViewFragment, WebViewFragment.TAG)
                    .commit();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        favoriteAction.setIcon(favoriteEntryUri == null ? R.drawable.ic_rating_not_favorite : R.drawable.ic_rating_favorite);
        return result;
    }

    @OptionsItem
    final void favoriteAction() {
        favoriteEntryUri = favoritesHelper.
                handleFavoriteMenuAction(favoriteEntryUri, resource, favoriteAction);
    }

    @OptionsItem
    final void refreshAction() {
        if (webViewFragment != null) {
            webViewFragment.refresh();
        }
    }

    @OptionsItem
    final void aboutAction() {
        SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
                .setTitle(resource.getLabel())
                .setMessage(resource.getDescription())
                .show();
    }

    @Override
    public void onWebViewCreated(WebViewFragment webViewFragment) {
        final GetServerInfoRequest request = new GetServerInfoRequest(jsRestClient);
        getSpiceManager().execute(request,
                new GetServerInfoRequestListener());
    }

    //---------------------------------------------------------------------
    // Inner classes
    //---------------------------------------------------------------------

    private class GetServerInfoRequestListener implements RequestListener<ServerInfo> {
        @Override
        public void onRequestFailure(SpiceException exception) {
            RequestExceptionHandler.handle(exception, DashboardHtmlViewerActivity.this, false);
        }

        @Override
        public void onRequestSuccess(ServerInfo data) {
            WebViewFragment webViewFragment = (WebViewFragment)
                    getSupportFragmentManager().findFragmentByTag(WebViewFragment.TAG);

            String dashboardUrl;
            String serverUrl = jsRestClient.getServerProfile().getServerUrl();

            dashboardUrl = serverUrl
                    + "/flow.html?_flowId=dashboardRuntimeFlow&viewAsDashboardFrame=true&dashboardResource="
                    + resource.getUri();
            if (data.getVersionCode() >= ServerInfo.VERSION_CODES.AMBER) {
                if (resource.getResourceType() == ResourceLookup.ResourceType.dashboard) {
                    try {
                        String url = URLEncoder.encode(resource.getUri(), "UTF-8");
                        dashboardUrl = serverUrl + "/dashboard/viewer.html?decorate=no#" + url;
                    } catch (UnsupportedEncodingException e) {
                        RequestExceptionHandler.handle(e, DashboardHtmlViewerActivity.this, false);
                        return;
                    }
                }
            }

            webViewFragment.loadUrl(dashboardUrl);
        }
    }

}
