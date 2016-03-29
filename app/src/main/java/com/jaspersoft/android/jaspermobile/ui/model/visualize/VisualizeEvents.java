package com.jaspersoft.android.jaspermobile.ui.model.visualize;

import rx.Observable;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public interface VisualizeEvents {
    Observable<Void> loadStartEvent();

    Observable<Void> scriptLoadedEvent();

    Observable<LoadCompleteEvent> loadCompleteEvent();

    Observable<ErrorEvent> loadErrorEvent();

    Observable<ReportCompleteEvent> reportCompleteEvent();

    Observable<PageLoadCompleteEvent> pageLoadCompleteEvent();

    Observable<PageLoadErrorEvent> pageLoadErrorEvent();

    Observable<MultiPageLoadEvent> multiPageLoadEvent();

    Observable<ExternalReferenceClickEvent> externalReferenceClickEvent();

    Observable<ExecutionReferenceClickEvent> executionReferenceClickEvent();

    Observable<ErrorEvent> windowErrorEvent();

    Observable<ErrorEvent> authErrorEvent();
}
