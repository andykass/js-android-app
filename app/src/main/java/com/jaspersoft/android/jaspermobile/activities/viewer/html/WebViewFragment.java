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

package com.jaspersoft.android.jaspermobile.activities.viewer.html;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;
import com.jaspersoft.android.jaspermobile.util.JSWebViewClient;
import com.jaspersoft.android.jaspermobile.util.ScrollableTitleHelper;
import com.jaspersoft.android.jaspermobile.webview.DefaultSessionListener;
import com.jaspersoft.android.jaspermobile.widget.JSWebView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

/**
 * This fragment should be removed. We won`t utilize it in future.
 *
 * @author Tom Koptel
 * @since 1.9
 */
@Deprecated
@EFragment(R.layout.html_viewer_layout)
public class WebViewFragment extends BaseFragment {

    public static final String TAG = WebViewFragment.class.getSimpleName();

    @ViewById
    FrameLayout webViewPlaceholder;
    @ViewById(R.id.htmlViewer_webView_progressBar)
    ProgressBar progressBar;

    @FragmentArg
    String resourceLabel;

    @InstanceState
    boolean mResourceLoaded;
    @InstanceState
    String currentUrl;

    @Bean
    ScrollableTitleHelper scrollableTitleHelper;

    @Inject
    JasperServer mServer;

    private OnWebViewCreated onWebViewCreated;
    private JSWebView webView;
    private JSWebViewClient jsWebViewClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        getBaseActivityComponent().inject(this);
    }

    @AfterViews
    final void init() {
        initWebView();
        scrollableTitleHelper.injectTitle(resourceLabel);

        jsWebViewClient = new JSWebViewClient(mServer.getBaseUrl());
        jsWebViewClient.setSessionListener(DefaultSessionListener.from(getActivity()));
    }

    private void initWebView() {
        // create new if necessary
        if (webView == null) createWebView();
        // attach to placeholder
        webViewPlaceholder.addView(webView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (webView != null) webViewPlaceholder.removeView(webView);
        super.onConfigurationChanged(newConfig);
        initWebView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    public void loadUrl(String url) {
        currentUrl = url;
        webView.loadUrl(url);
    }

    public void loadHtml(String baseUrl, String currentHtml) {
        webView.loadDataWithBaseURL(baseUrl, currentHtml, "text/html", "utf-8", null);
    }

    public void setOnWebViewCreated(OnWebViewCreated onWebViewCreated) {
        this.onWebViewCreated = onWebViewCreated;
    }

    public WebView getWebView() {
        return webView;
    }

    public boolean isResourceLoaded() {
        return mResourceLoaded;
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void createWebView() {
        webView = new JSWebView(getActivity(), null, R.style.htmlViewer_webView);
        prepareWebView();
        setWebViewClient();
        if (onWebViewCreated != null) {
            onWebViewCreated.onWebViewCreated(WebViewFragment.this);
        }
    }

    private void setWebViewClient() {
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // fade in
                if (progressBar.getAlpha() == 0) {
                    ObjectAnimator.ofFloat(progressBar, "alpha", 0f, 1f)
                            .setDuration(500).start();
                }
                // update value
                int maxProgress = progressBar.getMax();
                progressBar.setProgress((maxProgress / 100) * progress);
                // fade out
                if (progress == maxProgress) {
                    mResourceLoaded = true;
                    ObjectAnimator.ofFloat(progressBar, "alpha", 1f, 0f)
                            .setDuration(1000).start();
                }
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void prepareWebView() {
        // disable hardware acceleration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        // configure additional settings
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setWebViewClient(jsWebViewClient);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
    }

    public void refresh() {
        loadUrl(currentUrl);
    }

    public interface OnWebViewCreated {
        void onWebViewCreated(WebViewFragment webViewFragment);
    }

}
