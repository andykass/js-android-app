package com.jaspersoft.android.jaspermobile.activities.viewer.html.dashboard.webview.bridge;

/**
 * @author Tom Koptel
 * @since 2.0
 */
public interface DashboardCallback {
    void onMaximize(String title);
    void onMinimize();
    void onScriptLoaded();
    void onLoadStart();
    void onLoadDone();
    void onLoadError(String error);
}
