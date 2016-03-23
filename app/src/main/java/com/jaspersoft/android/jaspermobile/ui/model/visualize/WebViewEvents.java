package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import android.webkit.ConsoleMessage;

import rx.Observable;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public interface WebViewEvents {
    Observable<WebViewErrorEvent> receivedErrorEvent();
    Observable<Integer> progressChangedEvent();
    Observable<ConsoleMessage> consoleMessageEvent();
    Observable<Void> sessionExpiredEvent();
}
