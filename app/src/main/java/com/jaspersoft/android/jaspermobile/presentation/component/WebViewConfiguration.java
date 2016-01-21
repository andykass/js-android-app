package com.jaspersoft.android.jaspermobile.presentation.component;

import android.webkit.WebView;

import com.jaspersoft.android.jaspermobile.webview.SystemWebViewClient;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public final class WebViewConfiguration {
    private final WebView mWebView;
    private SystemWebViewClient mSystemWebViewClient;

    public WebViewConfiguration(WebView webView) {
        mWebView = webView;
    }

    public WebView getWebView() {
        return mWebView;
    }

    public void setSystemWebViewClient(SystemWebViewClient systemWebViewClient) {
        mSystemWebViewClient = systemWebViewClient;
    }

    public SystemWebViewClient getSystemWebViewClient() {
        return mSystemWebViewClient;
    }
}
