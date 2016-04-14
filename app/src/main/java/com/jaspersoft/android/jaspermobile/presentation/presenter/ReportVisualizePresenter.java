package com.jaspersoft.android.jaspermobile.presentation.presenter;

import android.support.annotation.VisibleForTesting;

import com.jaspersoft.android.jaspermobile.domain.ReportControlFlags;
import com.jaspersoft.android.jaspermobile.domain.ScreenCapture;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.VisualizeTemplate;
import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.interactor.profile.AuthorizeSessionUseCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.FlushInputControlsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetReportMetadataCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetReportShowControlsPropertyCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetVisualizeExecOptionsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetVisualizeTemplateCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.SaveScreenCaptureCase;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.presentation.contract.VisualizeReportContract;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.ErrorEvent;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.ExecutionReferenceClickEvent;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.ExternalReferenceClickEvent;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.LoadCompleteEvent;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.MultiPageLoadEvent;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.PageLoadCompleteEvent;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.PageLoadErrorEvent;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.ReportCompleteEvent;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.VisualizeComponent;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.VisualizeExecOptions;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.VisualizeViewModel;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.WebViewErrorEvent;
import com.jaspersoft.android.jaspermobile.presentation.navigation.MainPage;
import com.jaspersoft.android.jaspermobile.presentation.navigation.Navigator;
import com.jaspersoft.android.jaspermobile.presentation.navigation.Page;
import com.jaspersoft.android.jaspermobile.presentation.navigation.PageFactory;
import com.jaspersoft.android.jaspermobile.presentation.page.ReportPageState;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
public class ReportVisualizePresenter extends Presenter<VisualizeReportContract.View> implements VisualizeReportContract.Action {

    private final double mScreenDiagonal;
    private final String mReportUri;
    private final PostExecutionThread mPostExecutionThread;
    private final RequestExceptionHandler mRequestExceptionHandler;

    private final GetReportShowControlsPropertyCase mGetReportShowControlsPropertyCase;
    private final GetVisualizeTemplateCase mGetVisualizeTemplateCase;
    private final GetVisualizeExecOptionsCase mGetVisualizeExecOptionsCase;
    private final FlushInputControlsCase mFlushInputControlsCase;
    private final GetReportMetadataCase mGetReportMetadataCase;
    private final AuthorizeSessionUseCase mAuthorizeSessionUseCase;
    private final SaveScreenCaptureCase mSaveScreenCaptureCase;

    private CompositeSubscription mCompositeSubscription;

    @Inject
    public ReportVisualizePresenter(
            @Named("screen_diagonal") Double screenDiagonal,
            @Named("report_uri") String reportUri,
            PostExecutionThread postExecutionThread,
            RequestExceptionHandler requestExceptionHandler,
            GetReportShowControlsPropertyCase getReportShowControlsPropertyCase,
            GetVisualizeTemplateCase getVisualizeTemplateCase,
            GetVisualizeExecOptionsCase getVisualizeExecOptionsCase,
            FlushInputControlsCase flushInputControlsCase,
            GetReportMetadataCase getReportMetadataCase,
            AuthorizeSessionUseCase authorizeSessionUseCase,
            SaveScreenCaptureCase saveScreenCaptureCase) {
        mScreenDiagonal = screenDiagonal;
        mReportUri = reportUri;
        mPostExecutionThread = postExecutionThread;
        mRequestExceptionHandler = requestExceptionHandler;
        mGetReportShowControlsPropertyCase = getReportShowControlsPropertyCase;
        mGetVisualizeTemplateCase = getVisualizeTemplateCase;
        mGetVisualizeExecOptionsCase = getVisualizeExecOptionsCase;
        mFlushInputControlsCase = flushInputControlsCase;
        mGetReportMetadataCase = getReportMetadataCase;
        mAuthorizeSessionUseCase = authorizeSessionUseCase;
        mSaveScreenCaptureCase = saveScreenCaptureCase;
    }

    public void init() {
        if (getView() == null) {
            throw new NullPointerException("Please inject view before calling this method");
        }
        if (getView().getState().isControlsPageShown()) {
            boolean hasControls = getView().getState().hasControls();
            toggleFiltersAction(hasControls);
        } else {
            loadControls();
        }
    }

    private void loadControls() {
        getView().showLoading();
        mGetReportShowControlsPropertyCase.execute(mReportUri, new ErrorSubscriber<>(new SimpleSubscriber<ReportControlFlags>() {
            @Override
            public void onNext(ReportControlFlags flags) {
                getView().getState().setControlsPageShown(true);

                boolean hasControls = flags.hasControls();
                getView().getState().setHasControls(hasControls);
                toggleFiltersAction(hasControls);

                boolean needPrompt = flags.needPrompt();
                resolveNeedControls(needPrompt);
            }
        }));
    }

    @Override
    public void runReport() {
        loadVisualizeTemplate();
    }

    @Override
    public void loadPage(String pageRange) {
        getView().getVisualize().loadPage(pageRange);
    }

    @Override
    public void updateReport() {
        mGetVisualizeExecOptionsCase.execute(mReportUri, new ErrorSubscriber<>(new SimpleSubscriber<VisualizeExecOptions.Builder>() {
            @Override
            public void onNext(VisualizeExecOptions.Builder optionsBuilder) {
                // TODO fix nasty hack of passing view model to domain
                VisualizeExecOptions options = optionsBuilder.build();
                getView().resetZoom();
                getView().getVisualize().update(options.getParams());
            }
        }));
    }

    @Override
    public void refresh() {
        getView().showLoading();

        getView().showWebView(false);
        getView().showPagination(false);
        getView().showReloadButton(false);

        getView().resetPaginationControl();
        getView().setPaginationCurrentPage(1);

        ReportPageState state = getView().getState();
        state.setCurrentPage("1");

        if (state.isSessionExpired()) {
            reloadVisualize();
        } else {
            refreshVisualize();
        }
    }

    @Override
    public void shareReport(ScreenCapture screenCapture) {
        mSaveScreenCaptureCase.execute(screenCapture, new SimpleSubscriber<File>() {
            @Override
            public void onStart() {
                getView().showProgress();
            }

            @Override
            public void onError(Throwable e) {
                handleError(e);
            }

            @Override
            public void onNext(File item) {
                getView().hideLoading();
                getView().navigateToAnnotationPage(item);
            }
        });
    }

    private void reloadVisualize() {
        mAuthorizeSessionUseCase.execute(new ErrorSubscriber<>(new SimpleSubscriber<Void>() {
            @Override
            public void onCompleted() {
                getView().getState().setSessionExpired(false);
                runReport();
            }
        }));
    }

    private void refreshVisualize() {
        getView().getVisualize().refresh();
        getView().resetZoom();
    }

    private void resolveNeedControls(boolean needControls) {
        if (needControls) {
            getView().showInitialFiltersPage();
        } else {
            loadVisualizeTemplate();
        }
    }

    @VisibleForTesting
    void loadVisualizeTemplate() {
        double scale = mScreenDiagonal / 10.1;
        Map<String, Double> clientParams = Collections.singletonMap("initial_scale", scale);
        mGetVisualizeTemplateCase.execute(clientParams, new ErrorSubscriber<>(
                new SimpleSubscriber<VisualizeTemplate>() {
                    @Override
                    public void onNext(VisualizeTemplate template) {
                        getView().loadTemplateInView(template);
                    }
                }));
    }

    private void runReportOnVisualize() {
        mGetVisualizeExecOptionsCase.execute(mReportUri, new ErrorSubscriber<>(new SimpleSubscriber<VisualizeExecOptions.Builder>() {
            @Override
            public void onNext(VisualizeExecOptions.Builder builder) {
                VisualizeExecOptions options = builder.setDiagonal(mScreenDiagonal)
                        .build();
                getView().getVisualize().run(options);
            }
        }));
    }

    @Override
    public void resume() {
        mCompositeSubscription = new CompositeSubscription();
        subscribeToVisualizeEvents();
        subscribeToWebViewEvents();
    }

    @Override
    public void pause() {
        mCompositeSubscription.unsubscribe();
    }

    @Override
    public void destroy() {
        mGetReportShowControlsPropertyCase.unsubscribe();
        mGetVisualizeTemplateCase.unsubscribe();
        mGetVisualizeExecOptionsCase.unsubscribe();
        mAuthorizeSessionUseCase.unsubscribe();
        mFlushInputControlsCase.execute(mReportUri);
    }

    private void subscribeToWebViewEvents() {
        VisualizeViewModel visualize = getView().getVisualize();
        listenForProgressChanges(visualize);
        listenForReceivedError(visualize);
        listenForSessionExpiration(visualize);
    }

    private void listenForProgressChanges(VisualizeViewModel visualize) {
        subscribeToEvent(
                visualize.webViewEvents()
                        .progressChangedEvent()
                        .observeOn(mPostExecutionThread.getScheduler())
                        .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<Integer>() {
                            @Override
                            public void onNext(Integer progress) {
                                getView().updateDeterminateProgress(progress);
                            }
                        }))
        );
    }

    private void listenForReceivedError(VisualizeViewModel visualize) {
        subscribeToEvent(
                visualize.webViewEvents()
                        .receivedErrorEvent()
                        .observeOn(mPostExecutionThread.getScheduler())
                        .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<WebViewErrorEvent>() {
                            @Override
                            public void onNext(WebViewErrorEvent event) {
                                getView().hideLoading();
                                getView().showWebView(false);
                                getView().showError("title" + "\n" + "message");
                            }
                        }))
        );
    }

    private void listenForSessionExpiration(VisualizeViewModel visualize) {
        subscribeToEvent(
                visualize.webViewEvents()
                        .sessionExpiredEvent()
                        .observeOn(mPostExecutionThread.getScheduler())
                        .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<Void>() {
                            @Override
                            public void onNext(Void item) {
                                getView().handleSessionExpiration();
                            }
                        }))
        );
    }

    private void subscribeToVisualizeEvents() {
        VisualizeViewModel visualize = getView().getVisualize();
        listenForLoadStartEvent(visualize);
        listenScriptLoadedEvent(visualize);
        listenForLoadCompleteEvent(visualize);
        listenForLoadErrorEvent(visualize);
        listenForReportCompleteEvent(visualize);
        listenForPageLoadCompleteEvent(visualize);
        listenForPageLoadErrorEvent(visualize);
        listenForMultiPageLoadEvent(visualize);
        listenForExternalPageEvent(visualize);
        listenForExecutionEvent(visualize);
        listenForWindowErrorEvent(visualize);
        listenForAuthErrorEvent(visualize);
    }

    private void listenForLoadStartEvent(VisualizeComponent visualize) {
        subscribeToEvent(
                visualize.visualizeEvents()
                        .loadStartEvent()
                        .observeOn(mPostExecutionThread.getScheduler())
                        .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<Void>() {
                            @Override
                            public void onNext(Void item) {
                                getView().showLoading();
                                getView().showWebView(false);
                                getView().resetPaginationControl();
                            }
                        }))
        );
    }

    private void listenScriptLoadedEvent(VisualizeComponent visualize) {
        subscribeToEvent(
                visualize.visualizeEvents()
                        .scriptLoadedEvent()
                        .observeOn(mPostExecutionThread.getScheduler())
                        .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<Void>() {
                            @Override
                            public void onNext(Void item) {
                                runReportOnVisualize();
                            }
                        }))
        );
    }

    private void listenForLoadCompleteEvent(VisualizeViewModel visualize) {
        subscribeToEvent(
                visualize.visualizeEvents()
                        .loadCompleteEvent()
                        .observeOn(mPostExecutionThread.getScheduler())
                        .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<LoadCompleteEvent>() {
                            @Override
                            public void onNext(LoadCompleteEvent completeEvent) {
                                getView().hideLoading();
                                getView().showWebView(true);
                            }
                        }))
        );
    }

    private void listenForLoadErrorEvent(VisualizeViewModel visualize) {
        subscribeToEvent(
                visualize.visualizeEvents()
                        .loadErrorEvent()
                        .observeOn(mPostExecutionThread.getScheduler())
                        .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<ErrorEvent>() {
                            @Override
                            public void onNext(ErrorEvent errorEvent) {
                                getView().hideLoading();
                                if (errorEvent.isEmptyPage()) {
                                    getView().showEmptyPageMessage();
                                    getView().showPagination(false);
                                } else {
                                    String errorMessage = errorEvent.getErrorMessage();
                                    getView().showError(errorMessage);
                                }
                            }
                        }))
        );
    }


    private void listenForReportCompleteEvent(VisualizeViewModel visualize) {
        subscribeToEvent(
                visualize.visualizeEvents()
                        .reportCompleteEvent()
                        .observeOn(mPostExecutionThread.getScheduler())
                        .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<ReportCompleteEvent>() {
                            @Override
                            public void onNext(ReportCompleteEvent event) {
                                int totalPages = event.getTotalPages();

                                boolean hasContent = totalPages > 0;
                                toggleSaveAction(hasContent);

                                if (hasContent) {
                                    getView().hideEmptyPageMessage();

                                    boolean multiPage = totalPages > 1;
                                    getView().showPagination(multiPage);

                                    if (multiPage) {
                                        getView().setPaginationTotalPages(totalPages);
                                    }
                                } else {
                                    getView().showPagination(false);
                                    getView().showEmptyPageMessage();
                                }
                            }
                        }))
        );
    }

    private void listenForPageLoadCompleteEvent(VisualizeViewModel visualize) {
        subscribeToEvent(
                visualize.visualizeEvents()
                        .pageLoadCompleteEvent()
                        .observeOn(mPostExecutionThread.getScheduler())
                        .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<PageLoadCompleteEvent>() {
                            @Override
                            public void onNext(PageLoadCompleteEvent event) {
                                getView().showWebView(true);
                                getView().hideError();
                                getView().setPaginationEnabled(true);
                                getView().setPaginationCurrentPage(event.getPage());
                            }
                        }))
        );
    }

    private void listenForPageLoadErrorEvent(VisualizeViewModel visualize) {
        subscribeToEvent(
                visualize.visualizeEvents()
                        .pageLoadErrorEvent()
                        .observeOn(mPostExecutionThread.getScheduler())
                        .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<PageLoadErrorEvent>() {
                            @Override
                            public void onNext(PageLoadErrorEvent event) {
                                getView().setPaginationEnabled(true);
                                getView().setPaginationCurrentPage(event.getPage());
                                getView().showWebView(false);
                                getView().showError(event.getErrorMessage());
                            }
                        }))
        );
    }

    private void listenForMultiPageLoadEvent(VisualizeViewModel visualize) {
        subscribeToEvent(
                visualize.visualizeEvents()
                        .multiPageLoadEvent()
                        .observeOn(mPostExecutionThread.getScheduler())
                        .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<MultiPageLoadEvent>() {
                            @Override
                            public void onNext(MultiPageLoadEvent event) {
                                boolean needToShowPagination = event.isMultiPage() && getView().getPaginationTotalPages() > 1;
                                getView().showPagination(needToShowPagination);
                            }
                        }))
        );
    }

    private void listenForExternalPageEvent(VisualizeViewModel visualize) {
        subscribeToEvent(
                visualize.visualizeEvents()
                        .externalReferenceClickEvent()
                        .observeOn(mPostExecutionThread.getScheduler())
                        .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<ExternalReferenceClickEvent>() {
                            @Override
                            public void onNext(ExternalReferenceClickEvent event) {
                                getView().showExternalLink(event.getExternalReference());
                            }
                        }))
        );
    }

    private void listenForExecutionEvent(VisualizeViewModel visualize) {
        subscribeToEvent(
                visualize.visualizeEvents()
                        .executionReferenceClickEvent()
                        .observeOn(mPostExecutionThread.getScheduler())
                        .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<ExecutionReferenceClickEvent>() {
                            @Override
                            public void onNext(ExecutionReferenceClickEvent event) {
                                loadReportMetadata(event.getReportData());
                            }
                        }))
        );
    }

    private void loadReportMetadata(String reportData) {
        getView().showLoading();
        mGetReportMetadataCase.execute(reportData, new ErrorSubscriber<>(new SimpleSubscriber<ResourceLookup>() {
            @Override
            public void onCompleted() {
                getView().hideLoading();
            }

            @Override
            public void onNext(ResourceLookup lookup) {
                getView().executeReport(lookup);
            }
        }));
    }

    private void listenForWindowErrorEvent(VisualizeViewModel visualize) {
        subscribeToEvent(
                visualize.visualizeEvents()
                        .windowErrorEvent()
                        .observeOn(mPostExecutionThread.getScheduler())
                        .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<ErrorEvent>() {
                            @Override
                            public void onNext(ErrorEvent event) {
                                if (!event.isEmptyPage()) {
                                    getView().showError(event.getErrorMessage());
                                }
                            }
                        }))
        );
    }

    private void listenForAuthErrorEvent(VisualizeViewModel visualize) {
        subscribeToEvent(
                visualize.visualizeEvents()
                        .authErrorEvent()
                        .observeOn(mPostExecutionThread.getScheduler())
                        .subscribe(new ErrorSubscriber<>(new SimpleSubscriber<ErrorEvent>() {
                            @Override
                            public void onNext(ErrorEvent item) {
                                getView().hideLoading();
                                getView().getState().setSessionExpired(true);
                                getView().handleSessionExpiration();
                            }
                        }))
        );
    }

    @VisibleForTesting
    void handleError(Throwable error) {
        Timber.e(error, "Presenter received unexpected error");
        getView().hideLoading();
        getView().showError(mRequestExceptionHandler.extractMessage(error));
    }

    private void toggleFiltersAction(boolean visibility) {
        getView().showFilterAction(visibility);
        getView().reloadMenu();
    }

    private void toggleSaveAction(boolean visibility) {
        getView().showSaveAction(visibility);
        getView().reloadMenu();
    }

    private void subscribeToEvent(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    private final class ErrorSubscriber<R> extends SimpleSubscriber<R> {
        private final SimpleSubscriber<R> mDelegate;

        private ErrorSubscriber(SimpleSubscriber<R> delegate) {
            mDelegate = delegate;
        }

        @Override
        public void onError(Throwable e) {
            getView().hideLoading();
            handleError(e);
            mDelegate.onError(e);
        }

        @Override
        public void onCompleted() {
            getView().hideLoading();
            mDelegate.onCompleted();
        }

        @Override
        public void onNext(R item) {
            mDelegate.onNext(item);
        }
    }
}
