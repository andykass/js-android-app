package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import android.support.annotation.NonNull;

/**
 * @author Tom Koptel
 * @since 2.3
 */
final class SimpleWebViewComponent implements WebViewComponent {
    @NonNull
    private final WebViewEvents mWebViewEvents;

    public SimpleWebViewComponent(@NonNull WebViewEvents webViewEvents) {
        mWebViewEvents = webViewEvents;
    }

    @Override
    public WebViewEvents webViewEvents() {
        return mWebViewEvents;
    }
}
