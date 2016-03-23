package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import android.support.annotation.NonNull;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class VisualizeViewModel implements VisualizeComponent, WebViewComponent {
    @NonNull
    private final VisualizeComponent mVisualizeComponentDelegate;
    @NonNull
    private final WebViewComponent mWebViewComponentDelegate;

    private VisualizeViewModel(@NonNull VisualizeComponent visualizeComponentDelegate,
                               @NonNull WebViewComponent webViewComponentDelegate) {
        mVisualizeComponentDelegate = visualizeComponentDelegate;
        mWebViewComponentDelegate = webViewComponentDelegate;
    }

    @NonNull
    public static VisualizeViewModel newModel(WebViewConfiguration configuration) {
        WebViewEvents webViewEvents = new RxWebViewEvents(configuration);
        WebViewComponent webViewComponent = new SimpleWebViewComponent(webViewEvents);
        VisualizeEvents visualizeEvents = new RxVisualizeEvents(configuration);
        VisualizeComponent visualizeComponent = new SimpleVisualizeComponent(
                configuration.getWebView(), visualizeEvents);
        return new VisualizeViewModel(visualizeComponent, webViewComponent);
    }

    @NonNull
    @Override
    public VisualizeEvents visualizeEvents() {
        return mVisualizeComponentDelegate.visualizeEvents();
    }

    @NonNull
    @Override
    public VisualizeComponent run(@NonNull VisualizeExecOptions options) {
        return mVisualizeComponentDelegate.run(options);
    }

    @NonNull
    @Override
    public VisualizeComponent loadPage(String page) {
        return mVisualizeComponentDelegate.loadPage(page);
    }

    @NonNull
    @Override
    public VisualizeComponent update(@NonNull String jsonParams) {
        return mVisualizeComponentDelegate.update(jsonParams);
    }

    @NonNull
    @Override
    public VisualizeComponent refresh() {
        return mVisualizeComponentDelegate.refresh();
    }

    @Override
    public WebViewEvents webViewEvents() {
        return mWebViewComponentDelegate.webViewEvents();
    }
}
