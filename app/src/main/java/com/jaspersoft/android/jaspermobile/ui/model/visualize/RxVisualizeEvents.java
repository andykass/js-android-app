package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import com.jaspersoft.android.jaspermobile.webview.WebInterface;
import com.jaspersoft.android.jaspermobile.webview.report.bridge.ReportCallback;
import com.jaspersoft.android.jaspermobile.webview.report.bridge.ReportWebInterface;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public final class RxVisualizeEvents implements VisualizeEvents {
    private final PublishSubject<Void> mScriptLoaded = PublishSubject.create();
    private final PublishSubject<Void> mLoadStarted = PublishSubject.create();
    private final PublishSubject<LoadCompleteEvent> mLoadCompleteEvent = PublishSubject.create();
    private final PublishSubject<ErrorEvent> mLoadErrorEvent = PublishSubject.create();
    private final PublishSubject<ErrorEvent> mAuthErrorEvent = PublishSubject.create();
    private final PublishSubject<ReportCompleteEvent> mReportCompleteEvent = PublishSubject.create();
    private final PublishSubject<PageLoadCompleteEvent> mPageLoadCompleteEvent = PublishSubject.create();
    private final PublishSubject<PageLoadErrorEvent> mPageLoadErrorEvent = PublishSubject.create();
    private final PublishSubject<MultiPageLoadEvent> mMultiPageLoadEvent = PublishSubject.create();
    private final PublishSubject<ExternalReferenceClickEvent> mExternalReferenceClickEvent = PublishSubject.create();
    private final PublishSubject<ExecutionReferenceClickEvent> mExecutionReferenceClickEvent = PublishSubject.create();

    public RxVisualizeEvents(WebViewConfiguration configuration) {
        ReportCallback reportCallback = new ReportCallback() {
            @Override
            public void onScriptLoaded() {
                mScriptLoaded.onNext(null);
            }

            @Override
            public void onLoadStart() {
                mLoadStarted.onNext(null);
            }

            @Override
            public void onLoadDone(String parameters) {
                mLoadCompleteEvent.onNext(new LoadCompleteEvent(parameters));
            }

            @Override
            public void onLoadError(String error) {
                mLoadErrorEvent.onNext(new ErrorEvent(error));
            }

            @Override
            public void onAuthError(String error) {
                mAuthErrorEvent.onNext(new ErrorEvent(error));
            }

            @Override
            public void onReportCompleted(String status, int pages, String errorMessage) {
                if (status.equals("ready")) {
                    mReportCompleteEvent.onNext(new ReportCompleteEvent(pages));
                }
            }

            @Override
            public void onPageChange(int page) {
                mPageLoadCompleteEvent.onNext(new PageLoadCompleteEvent(page));
            }

            @Override
            public void onReferenceClick(String location) {
                mExternalReferenceClickEvent.onNext(new ExternalReferenceClickEvent(location));
            }

            @Override
            public void onReportExecutionClick(String data) {
                mExecutionReferenceClickEvent.onNext(new ExecutionReferenceClickEvent(data));
            }

            @Override
            public void onMultiPageStateObtained(boolean isMultiPage) {
                mMultiPageLoadEvent.onNext(new MultiPageLoadEvent(isMultiPage));
            }

            @Override
            public void onWindowError(String errorMessage) {
                mLoadErrorEvent.onNext(new ErrorEvent(errorMessage));
            }

            @Override
            public void onPageLoadError(String errorMessage, int page) {
                mPageLoadErrorEvent.onNext(new PageLoadErrorEvent(errorMessage, page));
            }
        };
        WebInterface webInterface = ReportWebInterface.from(reportCallback);
        webInterface.exposeJavascriptInterface(configuration.getWebView());
    }

    @Override
    public Observable<Void> scriptLoadedEvent() {
        return mScriptLoaded;
    }

    @Override
    public Observable<Void> loadStartEvent() {
        return mLoadStarted;
    }

    @Override
    public Observable<LoadCompleteEvent> loadCompleteEvent() {
        return mLoadCompleteEvent;
    }

    @Override
    public Observable<ErrorEvent> loadErrorEvent() {
        return mLoadErrorEvent;
    }

    @Override
    public Observable<ReportCompleteEvent> reportCompleteEvent() {
        return mReportCompleteEvent;
    }

    @Override
    public Observable<PageLoadCompleteEvent> pageLoadCompleteEvent() {
        return mPageLoadCompleteEvent;
    }

    @Override
    public Observable<PageLoadErrorEvent> pageLoadErrorEvent() {
        return mPageLoadErrorEvent;
    }

    @Override
    public Observable<MultiPageLoadEvent> multiPageLoadEvent() {
        return mMultiPageLoadEvent;
    }

    @Override
    public Observable<ExternalReferenceClickEvent> externalReferenceClickEvent() {
        return mExternalReferenceClickEvent;
    }

    @Override
    public Observable<ExecutionReferenceClickEvent> executionReferenceClickEvent() {
        return mExecutionReferenceClickEvent;
    }

    @Override
    public Observable<ErrorEvent> windowErrorEvent() {
        return mLoadErrorEvent;
    }

    @Override
    public Observable<ErrorEvent> authErrorEvent() {
        return mAuthErrorEvent;
    }
}
