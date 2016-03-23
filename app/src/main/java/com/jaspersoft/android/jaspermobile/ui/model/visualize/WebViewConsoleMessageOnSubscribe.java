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
final class WebViewConsoleMessageOnSubscribe implements Observable.OnSubscribe<ConsoleMessage> {
    @NonNull
    private final WebViewConfiguration mConfiguration;

    public WebViewConsoleMessageOnSubscribe(@NonNull WebViewConfiguration configuration) {
        mConfiguration = configuration;
    }

    @Override
    public void call(final Subscriber<? super ConsoleMessage> subscriber) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException(
                    "Must be called from the main thread. Was: " + Thread.currentThread());
        }

        final WebView webView = mConfiguration.getWebView();
        final SystemChromeClient systemChromeClient = mConfiguration.getSystemChromeClient();
        SystemChromeClient client = systemChromeClient
                .newBuilder(webView.getContext())
                .withDelegateListener(new JasperChromeClientListener() {
                    @Override
                    public void onProgressChanged(WebView view, int progress) {
                        systemChromeClient.getDelegate().onProgressChanged(view, progress);
                    }

                    @Override
                    public void onConsoleMessage(ConsoleMessage consoleMessage) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(consoleMessage);
                        }
                    }
                })
                .build();
        mConfiguration.setSystemChromeClient(client);

        WebViewEnvironment.configure(webView)
                .withChromeClient(client);
    }
}
