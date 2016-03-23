package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.webkit.WebView;

import com.jaspersoft.android.jaspermobile.webview.ErrorWebViewClientListener;
import com.jaspersoft.android.jaspermobile.webview.JasperWebViewClientListener;
import com.jaspersoft.android.jaspermobile.webview.SystemWebViewClient;
import com.jaspersoft.android.jaspermobile.webview.TimeoutWebViewClientListener;
import com.jaspersoft.android.jaspermobile.webview.WebViewEnvironment;

import rx.Observable;
import rx.Subscriber;

/**
 * @author Tom Koptel
 * @since 2.3
 */
final class WebViewErrorReceivedOnSubscribe  implements Observable.OnSubscribe<WebViewErrorEvent> {
    @NonNull
    private final WebViewConfiguration mConfiguration;

    WebViewErrorReceivedOnSubscribe(@NonNull WebViewConfiguration configuration) {
        mConfiguration = configuration;
    }

    @Override
    public void call(final Subscriber<? super WebViewErrorEvent> subscriber) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException(
                    "Must be called from the main thread. Was: " + Thread.currentThread());
        }

        WebView webView = mConfiguration.getWebView();
        JasperWebViewClientListener errorListener = new ErrorWebViewClientListener(webView.getContext(),
                new ErrorWebViewClientListener.OnWebViewErrorListener() {
                    @Override
                    public void onWebViewError(String title, String message, String failingUrl, int errorCode) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(new WebViewErrorEvent(title, message));
                        }
                    }
                });
        JasperWebViewClientListener clientListener = TimeoutWebViewClientListener.wrap(errorListener);

        SystemWebViewClient client = mConfiguration.getSystemWebViewClient()
                .newBuilder()
                .withDelegateListener(clientListener)
                .build();
        mConfiguration.setSystemWebViewClient(client);

        WebViewEnvironment.configure(webView)
                .withWebClient(client);
    }
}
