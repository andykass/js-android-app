package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import android.support.annotation.NonNull;
import android.webkit.ConsoleMessage;

import rx.Observable;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public final class RxWebViewEvents implements WebViewEvents {
    @NonNull
    private final WebViewConfiguration mConfiguration;

    RxWebViewEvents(@NonNull WebViewConfiguration configuration) {
        mConfiguration = configuration;
    }

    @Override
    public Observable<WebViewErrorEvent> receivedErrorEvent() {
        return Observable.create(new WebViewErrorReceivedOnSubscribe(mConfiguration));
    }

    @Override
    public Observable<Integer> progressChangedEvent() {
        return Observable.create(new WebViewProgressChangeOnSubscribe(mConfiguration));
    }

    @Override
    public Observable<ConsoleMessage> consoleMessageEvent() {
        return Observable.create(new WebViewConsoleMessageOnSubscribe(mConfiguration));
    }

    @Override
    public Observable<Void> sessionExpiredEvent() {
        return Observable.create(new WebViewSessionExpiredOnSubscribe(mConfiguration));
    }
}
