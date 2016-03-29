package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import android.os.Looper;
import android.webkit.WebView;

import com.jaspersoft.android.jaspermobile.webview.DefaultUrlPolicy;
import com.jaspersoft.android.jaspermobile.webview.SystemWebViewClient;
import com.jaspersoft.android.jaspermobile.webview.UrlPolicy;
import com.jaspersoft.android.jaspermobile.webview.WebViewEnvironment;

import rx.Observable;
import rx.Subscriber;

/**
 * @author Tom Koptel
 * @since 2.3
 */
final class WebViewSessionExpiredOnSubscribe implements Observable.OnSubscribe<Void> {
    private final WebViewConfiguration mConfiguration;

    public WebViewSessionExpiredOnSubscribe(WebViewConfiguration configuration) {
        mConfiguration = configuration;
    }

    @Override
    public void call(final Subscriber<? super Void> subscriber) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException(
                    "Must be called from the main thread. Was: " + Thread.currentThread());
        }

        WebView webView = mConfiguration.getWebView();
        String serverUrl = mConfiguration.getServerUrl();

        UrlPolicy defaultPolicy = new DefaultUrlPolicy(serverUrl)
                .withSessionListener(new DefaultUrlPolicy.SessionListener() {
                    @Override
                    public void onSessionExpired() {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(null);
                        }
                    }
                });

        SystemWebViewClient systemWebViewClient = mConfiguration.getSystemWebViewClient();
        SystemWebViewClient client = systemWebViewClient.newBuilder()
                .registerUrlPolicy(defaultPolicy)
                .build();
        mConfiguration.setSystemWebViewClient(client);

        WebViewEnvironment.configure(webView)
                .withWebClient(client);
    }
}
