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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.jaspersoft.android.jaspermobile.BuildConfig;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboToolbarActivity;
import com.jaspersoft.android.jaspermobile.dialog.LogDialog;
import com.jaspersoft.android.jaspermobile.util.FavoritesHelper_;
import com.jaspersoft.android.jaspermobile.util.JSWebViewClient_;
import com.jaspersoft.android.jaspermobile.webview.JasperChromeClientListener;
import com.jaspersoft.android.jaspermobile.webview.JasperWebViewClientListener;
import com.jaspersoft.android.jaspermobile.webview.SystemChromeClient;
import com.jaspersoft.android.jaspermobile.webview.SystemWebViewClient;
import com.jaspersoft.android.jaspermobile.webview.WebViewEnvironment;
import com.jaspersoft.android.jaspermobile.webview.dashboard.DashboardRequestInterceptor;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

/**
 * Activity that performs dashboard viewing in HTML format through Cordova native component.
 *
 * @author Tom Koptel
 * @since 2.0
 */
public abstract class DashboardCordovaActivity extends RoboToolbarActivity implements CordovaInterface {
    public final static String RESOURCE_EXTRA = "resource";

    protected CordovaWebView webView;
    private ProgressBar progressBar;
    private JSWebViewClient_ jsWebViewClient;
    private ChromeClient chromeClient;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    protected ResourceLookup resource;
    private MenuItem favoriteAction;

    private Uri favoriteEntryUri;
    private FavoritesHelper_ favoritesHelper;
    private final Handler mHandler = new Handler();
    private final Runnable mZoomOutTask = new Runnable() {
        @Override
        public void run() {
            if (webView.zoomOut()) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cordova_dashboard_viewer);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            resource = extras.getParcelable(RESOURCE_EXTRA);
        }

        favoritesHelper = FavoritesHelper_.getInstance_(this);
        if (savedInstanceState == null && resource != null) {
            favoriteEntryUri = favoritesHelper.queryFavoriteUri(resource);
        }

        webView = (CordovaWebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        jsWebViewClient = JSWebViewClient_.getInstance_(this);

        setupSettings();
        initCordovaWebView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard_menu, menu);
        favoriteAction = menu.findItem(R.id.favoriteAction);

        favoriteAction.setIcon(favoriteEntryUri == null ? R.drawable.ic_menu_star_outline : R.drawable.ic_menu_star);
        favoriteAction.setTitle(favoriteEntryUri == null ? R.string.r_cm_add_to_favorites : R.string.r_cm_remove_from_favorites);

        if (isDebugOrQa()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.debug, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.refreshAction) {
            onRefresh();
        }
        if (itemId == R.id.aboutAction) {
            aboutAction();
        }
        if (itemId == R.id.favoriteAction) {
            favoriteAction();
        }
        if (itemId == R.id.showLog) {
            showLog();
        }
        if (itemId == android.R.id.home) {
            onHomeAsUpCalled();
        }

        return true;
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mZoomOutTask);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (this.webView != null) {
            webView.handleDestroy();
        }
    }

    protected void resetZoom() {
        mZoomOutTask.run();
    }

    private void favoriteAction() {
        favoriteEntryUri = favoritesHelper.
                handleFavoriteMenuAction(favoriteEntryUri, resource, favoriteAction);
    }


    private void aboutAction() {
        SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
                .setTitle(resource.getLabel())
                .setMessage(resource.getDescription())
                .setNegativeButtonText(android.R.string.ok)
                .show();
    }

    private void showLog() {
        if (chromeClient != null) {
            LogDialog.create(getSupportFragmentManager(), chromeClient.messages);
        }
    }

    @Override
    public void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode) {
    }

    @Override
    public void setActivityResultCallback(CordovaPlugin plugin) {
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public final Object onMessage(String message, Object data) {
        if ("onPageFinished".equals(message)) {
            onPageFinished();
        }
        return null;
    }

    @Override
    public final ExecutorService getThreadPool() {
        return executorService;
    }

    public abstract void setupWebView(WebView webView);

    public abstract void onPageFinished();

    public abstract void onRefresh();

    public abstract void onHomeAsUpCalled();

    private void setupSettings() {
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        }

        if (isDebugOrQa()) {
            enableDebug();
        }
    }

    private boolean isDebugOrQa() {
        return BuildConfig.FLAVOR.equals("qa") || BuildConfig.DEBUG;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void enableDebug() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    private void initCordovaWebView() {
        JasperChromeClientListener chromeClientListener = new JasperChromeClientListener() {
            @Override
            public void onProgressChanged(WebView view, int progress) {

            }

            @Override
            public void onConsoleMessage(ConsoleMessage consoleMessage) {

            }
        };
        JasperWebViewClientListener webClientListener = new JasperWebViewClientListener() {
            @Override
            public void onPageStarted(String newUrl) {
            }

            @Override
            public void onReceivedError(int errorCode, String description, String failingUrl) {
            }

            @Override
            public void onPageFinishedLoading(String url) {
            }
        };

        SystemChromeClient systemChromeClient = SystemChromeClient.from(this)
                .withDelegateListener(chromeClientListener);
        SystemWebViewClient systemWebViewClient = SystemWebViewClient.newInstance()
                .withDelegateListener(webClientListener)
                .withInterceptor(DashboardRequestInterceptor.newInstance());

        WebViewEnvironment.configure(webView)
                .withChromeClient(systemChromeClient)
                .withWebClient(systemWebViewClient);

//        Whitelist whitelist = new Whitelist();
//        whitelist.addWhiteListEntry("http://*/*", true);
//        whitelist.addWhiteListEntry("https://*/*", true);
//        CordovaPreferences cordovaPreferences = new CordovaPreferences();
//
//        jsWebViewClient.setSessionListener(new SessionListener(getActivity()));
//        CordovaWebViewClient webViewClient2 = new DashboardCordovaWebClient(this, webView, jsWebViewClient);
//
//        this.chromeClient = new ChromeClient(this, webView);
//
//        List<PluginEntry> pluginEntries = (List<PluginEntry>) Collections.EMPTY_LIST;
//
//        setupWebView(webView);
//        webView.init(this, webViewClient2, chromeClientListener, pluginEntries, whitelist, whitelist, cordovaPreferences);
    }

    //---------------------------------------------------------------------
    // Inner classes
    //---------------------------------------------------------------------

    private class ChromeClient extends CordovaChromeClient {
        private final List<ConsoleMessage> messages = new LinkedList<ConsoleMessage>();

        public ChromeClient(CordovaInterface ctx, CordovaWebView app) {
            super(ctx, app);
        }

        @Override
        public void onProgressChanged(WebView view, int progress) {
            int maxProgress = progressBar.getMax();
            progressBar.setProgress((maxProgress / 100) * progress);
            if (progress == maxProgress) {
                progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            messages.add(consoleMessage);
            return super.onConsoleMessage(consoleMessage);
        }
    }
}
