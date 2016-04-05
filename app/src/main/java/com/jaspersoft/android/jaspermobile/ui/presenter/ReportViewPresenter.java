package com.jaspersoft.android.jaspermobile.ui.presenter;

import android.support.annotation.VisibleForTesting;

import com.jaspersoft.android.jaspermobile.domain.PageRequest;
import com.jaspersoft.android.jaspermobile.domain.ReportPage;
import com.jaspersoft.android.jaspermobile.domain.ReportControlFlags;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.FlushInputControlsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.FlushReportCachesCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetReportMultiPagePropertyCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetReportPageContentCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetReportShowControlsPropertyCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetReportTotalPagesPropertyCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.ReloadReportCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.RunReportCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.UpdateReportCase;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.contract.RestReportContract;
import com.jaspersoft.android.jaspermobile.ui.page.ReportPageState;
import com.jaspersoft.android.sdk.service.exception.ServiceException;
import com.jaspersoft.android.sdk.service.exception.StatusCodes;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Subscriber;
import timber.log.Timber;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerActivity
public class ReportViewPresenter extends LegacyPresenter<RestReportContract.View> implements RestReportContract.Action {
    private final String mReportUri;
    private final RequestExceptionHandler mExceptionHandler;
    private final GetReportShowControlsPropertyCase mGetReportShowControlsPropertyCase;
    private final GetReportMultiPagePropertyCase mGetReportMultiPagePropertyCase;
    private final GetReportTotalPagesPropertyCase mGetReportTotalPagesPropertyCase;
    private final GetReportPageContentCase mGetReportPageContentCase;
    private final RunReportCase mRunReportCase;
    private final UpdateReportCase mUpdateReportCase;
    private final ReloadReportCase mReloadReportCase;
    private final FlushReportCachesCase mFlushReportCachesCase;
    private final FlushInputControlsCase mFlushInputControlsCase;

    @Inject
    public ReportViewPresenter(
            @Named("report_uri") String reportUri,
            RequestExceptionHandler exceptionHandler,
            GetReportShowControlsPropertyCase getReportShowControlsPropertyCase,
            GetReportMultiPagePropertyCase getReportMultiPagePropertyCase,
            GetReportTotalPagesPropertyCase getReportTotalPagesPropertyCase,
            GetReportPageContentCase getReportPageContentCase,
            RunReportCase runReportCase,
            UpdateReportCase updateReportCase,
            ReloadReportCase reloadReportCase,
            FlushReportCachesCase flushReportCachesCase,
            FlushInputControlsCase flushInputControlsCase
    ) {
        mReportUri = reportUri;
        mExceptionHandler = exceptionHandler;
        mGetReportShowControlsPropertyCase = getReportShowControlsPropertyCase;
        mGetReportMultiPagePropertyCase = getReportMultiPagePropertyCase;
        mGetReportTotalPagesPropertyCase = getReportTotalPagesPropertyCase;
        mGetReportPageContentCase = getReportPageContentCase;
        mRunReportCase = runReportCase;
        mUpdateReportCase = updateReportCase;
        mReloadReportCase = reloadReportCase;
        mFlushReportCachesCase = flushReportCachesCase;
        mFlushInputControlsCase = flushInputControlsCase;
    }

    public void init() {
        if (getView() == null) {
            throw new NullPointerException("Please inject view before calling this method");
        }
        ReportPageState state = getView().getState();
        if (state.isControlsPageShown() && state.getCurrentPage() != null) {
            loadLastSavedPage();
            loadMultiPageProperty();
            loadTotalPagesProperty();

            boolean hasControls = state.hasControls();
            toggleFiltersAction(hasControls);
        } else {
            loadReportMetadata();
        }
    }

    private void loadLastSavedPage() {
        loadPageByPosition(getView().getState().getCurrentPage());
    }

    private void loadPageByPosition(final String position) {
        PageRequest request = new PageRequest.Builder()
                .setUri(mReportUri)
                .setRange(position)
                .build();
        mGetReportPageContentCase.execute(request, new ErrorSubscriber<>(new SimpleSubscriber<ReportPage>() {
            @Override
            public void onStart() {
                getView().showWebView(false);
                getView().showPageLoader(true);
            }

            @Override
            public void onNext(ReportPage item) {
                if (item.isEmpty()) {
                    handleExportOutOfRange();
                } else {
                    showPage(position, item);
                }
            }

            @Override
            public void onError(Throwable e) {
                getView().showPageLoader(false);
            }
        }));
    }

    private void loadReportMetadata() {
        showLoading();
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

    private void resolveNeedControls(boolean needControls) {
        if (needControls) {
            getView().showInitialFiltersPage();
        } else {
            runReport();
        }
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void destroy() {
        mGetReportShowControlsPropertyCase.unsubscribe();
        mGetReportMultiPagePropertyCase.unsubscribe();
        mGetReportTotalPagesPropertyCase.unsubscribe();
        mGetReportPageContentCase.unsubscribe();
        mRunReportCase.unsubscribe();
        mUpdateReportCase.unsubscribe();
        mReloadReportCase.unsubscribe();
        mFlushReportCachesCase.execute(mReportUri);
        mFlushInputControlsCase.execute(mReportUri);
    }

    @Override
    public void loadPage(String pageRange) {
        getView().getState().setRequestedPage(pageRange);
        loadPageByPosition(pageRange);
    }

    @Override
    public void runReport() {
        showLoading();
        mRunReportCase.execute(mReportUri, new ErrorSubscriber<>(new SimpleSubscriber<ReportPage>() {
            @Override
            public void onNext(ReportPage page) {
                showPageInView("1", page);
            }
        }));
    }

    @VisibleForTesting
    void loadMultiPageProperty() {
        mGetReportMultiPagePropertyCase.execute(mReportUri, new ErrorSubscriber<>(new SimpleSubscriber<Boolean>() {
            @Override
            public void onNext(Boolean multiPage) {
                ReportPageState state = getView().getState();
                Integer totalPages = state.getTotalPages();
                if (totalPages != null) {
                    multiPage &= totalPages > 1;
                }

                togglePaginationControl(multiPage);
            }
        }));
    }

    @VisibleForTesting
    void loadTotalPagesProperty() {
        mGetReportTotalPagesPropertyCase.execute(mReportUri, new ErrorSubscriber<>(new SimpleSubscriber<Integer>() {
            @Override
            public void onNext(Integer totalPages) {
                ReportPageState state = getView().getState();
                state.setTotalPages(totalPages);

                boolean hasNoPages = (totalPages == 0);
                toggleSaveAction(!hasNoPages);

                boolean multiPage = (totalPages > 1);
                togglePaginationControl(multiPage);

                if (hasNoPages) {
                    showEmptyPage();
                } else {
                    updateTotalPagesLabel(totalPages);
                    loadLastSavedPage();
                }
            }
        }));
    }

    @Override
    public void updateReport() {
        showLoading();
        resetTotalPagesLabel();
        mUpdateReportCase.execute(mReportUri, new ErrorSubscriber<>(new SimpleSubscriber<ReportPage>() {
            @Override
            public void onNext(ReportPage page) {
                showPageInView("1", page);
            }
        }));
    }

    @Override
    public void refresh() {
        reloadByPosition("1");
    }

    private void reloadByPosition(final String position) {
        showLoading();
        resetTotalPagesLabel();
        PageRequest request = new PageRequest.Builder()
                .setUri(mReportUri)
                .setRange(position)
                .build();
        mReloadReportCase.execute(request, new ErrorSubscriber<>(new SimpleSubscriber<ReportPage>() {
            @Override
            public void onNext(ReportPage page) {
                showPageInView(position, page);
            }
        }));
    }

    private void showPageInView(String position, ReportPage page) {
        if (page.isEmpty()) {
            showEmptyPage();
        } else {
            showPage(position, page);
            loadMultiPageProperty();
            loadTotalPagesProperty();
        }
    }

    private void showPageOutOfRangeError() {
        getView().showPageOutOfRangeError();
    }

    private void showEmptyPage() {
        getView().showEmptyPageMessage();
    }

    private void resetTotalPagesLabel() {
        getView().resetPaginationControl();
    }

    private void updateTotalPagesLabel(int pages) {
        getView().showTotalPages(pages);
    }

    private void toggleSaveAction(boolean visibility) {
        getView().setSaveActionVisibility(visibility);
        getView().reloadMenu();
    }

    private void toggleFiltersAction(boolean visibility) {
        getView().setFilterActionVisibility(visibility);
        getView().reloadMenu();
    }

    private void togglePaginationControl(boolean multiPage) {
        getView().showPaginationControl(multiPage);
    }

    private void hideLoading() {
        getView().hideLoading();
    }

    private void showLoading() {
        getView().showLoading();
    }

    @VisibleForTesting
    void showPage(String pagePosition, ReportPage page) {
        getView().hideError();
        getView().showCurrentPage(Integer.valueOf(pagePosition));
        getView().getState().setCurrentPage(pagePosition);
        getView().showPage(new String(page.getContent()));
    }

    @VisibleForTesting
    void handleError(Throwable error) {
        if (error instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) error;
            tryToRecoverFromSdkError(serviceException);
        } else {
            Timber.e(error, "Presenter received unexpected error");
            showErrorMessage(error);
        }
    }

    private void tryToRecoverFromSdkError(ServiceException serviceException) {
        int errorCode = serviceException.code();
        switch (errorCode) {
            case StatusCodes.EXPORT_PAGE_OUT_OF_RANGE:
                handleExportOutOfRange();
                break;
            case StatusCodes.REPORT_EXECUTION_INVALID:
                reloadByPosition(getView().getState().getRequestedPage());
                break;
            default:
                Timber.e(serviceException, "Page request operation crashed with SDK exception");
                showErrorMessage(serviceException);
        }
    }

    private void handleExportOutOfRange() {
        showPageOutOfRangeError();
        loadLastSavedPage();
    }

    private void showErrorMessage(Throwable error) {
        getView().hideLoading();
        getView().showError(mExceptionHandler.extractMessage(error));
    }

    private class ErrorSubscriber<R> extends Subscriber<R> {
        private final Subscriber<R> mDelegate;

        private ErrorSubscriber(Subscriber<R> delegate) {
            mDelegate = delegate;
        }

        @Override
        public void onStart() {
            getView().hideError();
            mDelegate.onStart();
        }

        @Override
        public void onCompleted() {
            hideLoading();
            mDelegate.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "Error on REST Report Presenter");
            handleError(e);
            hideLoading();
            mDelegate.onError(e);
        }

        @Override
        public void onNext(R r) {
            mDelegate.onNext(r);
        }
    }
}
