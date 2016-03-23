package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.webkit.ConsoleMessage;
import android.webkit.WebView;

import com.jaspersoft.android.jaspermobile.webview.JasperChromeClientListener;
import com.jaspersoft.android.jaspermobile.webview.SystemChromeClient;
import com.jaspersoft.android.jaspermobile.webview.WebViewEnvironment;

import rx.Observable;
import rx.Subscriber;

/**
 * @author Tom Koptel
 * @since 2.3
 */
final class WebViewProgressChangeOnSubscribe implements Observable.OnSubscribe<Integer> {
    @NonNull
    private final WebViewConfiguration mConfiguration;

    public WebViewProgressChangeOnSubscribe(@NonNull WebViewConfiguration configuration) {
        mConfiguration = configuration;
    }

    @Override
    public void call(final Subscriber<? super Integer> subscriber) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException(
                    "Must be called from the main thread. Was: " + Thread.currentThread());
        }

        WebView webView = mConfiguration.getWebView();
        final SystemChromeClient systemChromeClient = mConfiguration.getSystemChromeClient();
        SystemChromeClient client = systemChromeClient
                .newBuilder(webView.getContext())
                .withDelegateListener(new JasperChromeClientListener() {
                    @Override
                    public void onProgressChanged(WebView view, int progress) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(progress);
                        }
                    }

                    @Override
                    public void onConsoleMessage(ConsoleMessage consoleMessage) {
                        systemChromeClient.getDelegate().onConsoleMessage(consoleMessage);
                    }
                })
                .build();
        mConfiguration.setSystemChromeClient(client);

        WebViewEnvironment.configure(webView)
                .withChromeClient(client);
    }
}
