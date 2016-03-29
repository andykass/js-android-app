package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import android.webkit.WebView;

import com.jaspersoft.android.jaspermobile.webview.SystemChromeClient;
import com.jaspersoft.android.jaspermobile.webview.SystemWebViewClient;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public final class WebViewConfiguration {
    private final WebView mWebView;
    private final String mServerUrl;
    private SystemWebViewClient mSystemWebViewClient;
    private SystemChromeClient mSystemChromeClient;

    public WebViewConfiguration(WebView webView, String serverUrl) {
        mWebView = webView;
        mServerUrl = serverUrl;
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

    public SystemChromeClient getSystemChromeClient() {
        return mSystemChromeClient;
    }

    public void setSystemChromeClient(SystemChromeClient systemChromeClient) {
        mSystemChromeClient = systemChromeClient;
    }

    public String getServerUrl() {
        return mServerUrl;
    }
}
