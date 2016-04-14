package com.jaspersoft.android.jaspermobile.presentation.presenter;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.FakePostExecutionThread;
import com.jaspersoft.android.jaspermobile.FakePreExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.PageRequest;
import com.jaspersoft.android.jaspermobile.domain.ReportPage;
import com.jaspersoft.android.jaspermobile.domain.ScreenCapture;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetReportMultiPagePropertyCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetReportPageContentCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetReportTotalPagesPropertyCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.ReloadReportCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.RunReportCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.UpdateReportCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.SaveScreenCaptureCase;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.presentation.contract.RestReportContract;
import com.jaspersoft.android.jaspermobile.presentation.page.ReportPageState;
import com.jaspersoft.android.sdk.service.exception.ServiceException;
import com.jaspersoft.android.sdk.service.exception.StatusCodes;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class ReportViewPresenterTest {
    private static final String REPORT_URI = "my/uri";
    private static final String PAGE_CONTENT = "page content";
    private static final ReportPage PAGE = new ReportPage(PAGE_CONTENT.getBytes(), true);
    private static final String CURRENT_PAGE = "10";
    private static final String REQUESTED_PAGE = "11";
    private static final PageRequest CURRENT_PAGE_REQUEST = new PageRequest.Builder().setUri(REPORT_URI).setRange(CURRENT_PAGE).asHtml().build();
    private static final PageRequest NEW_PAGE_REQUEST = new PageRequest.Builder().setUri(REPORT_URI).setRange(REQUESTED_PAGE).asHtml().build();
    private static final File SCREEN_CAPTURED_FILE = new File("my_file.jpg");

    @Mock
    RequestExceptionHandler mExceptionHandler;
    @Mock
    RestReportContract.View mView;

    private ReportViewPresenter presenter;
    private FakeGetReportShowControlsPropertyCase mFakeGetReportShowControlsPropertyCase;
    private FakeGetReportPageContentCase mFakeGetReportPageContentCase;
    private FakeGetReportMultiPagePropertyCase mFakeGetReportMultiPagePropertyCase;
    private FakeGetReportTotalPagesPropertyCase mFakeGetReportTotalPagesPropertyCase;
    private FakeRunReportCase mFakeRunReportCase;
    private FakeUpdateReportCase mFakeUpdateReportCase;
    private FakeReloadReportCase mFakeReloadReportCase;
    private FakeFlushReportCachesCase mFakeFlushReportCachesCase;
    private FakeFlushInputControlsCase mFakeFlushInputControlsCase;
    private FakeSaveScreenCaptureCase mFakeSaveScreenCaptureCase;

    private ReportPageState mReportPageState;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        setupMocks();
        presenter = new ReportViewPresenter(
                REPORT_URI,
                mExceptionHandler,
                mFakeGetReportShowControlsPropertyCase,
                mFakeGetReportMultiPagePropertyCase,
                mFakeGetReportTotalPagesPropertyCase,
                mFakeGetReportPageContentCase,
                mFakeRunReportCase,
                mFakeUpdateReportCase,
                mFakeReloadReportCase,
                mFakeFlushReportCachesCase,
                mFakeFlushInputControlsCase,
                mFakeSaveScreenCaptureCase
        );
        presenter.injectView(mView);
    }

    @Test
    public void should_handle_screen_capture_error() throws Exception {
        when(mExceptionHandler.extractMessage(any(Throwable.class))).thenReturn("Opps!");
        when(mFakeSaveScreenCaptureCase.buildUseCaseObservable(any(ScreenCapture.class)))
                .thenReturn(Observable.<File>error(new IOException()));

        presenter.shareReport(null);
        verify(mView).showError("Opps!");
    }

    @Test
    public void should_handle_screen_capture_success() throws Exception {
        presenter.shareReport(null);
        verify(mView).navigateToAnnotationPage(SCREEN_CAPTURED_FILE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_show_filters_page_if_check_for_controls_was_positive() throws Exception {
        mFakeGetReportShowControlsPropertyCase.setNeedParams(true);
        presenter.init();

        verify(mView).showLoading();
        verify(mFakeGetReportShowControlsPropertyCase).execute(eq(REPORT_URI), any(Subscriber.class));
        verify(mView).setFilterActionVisibility(true);
        verify(mView).hideLoading();

        verify(mView).showInitialFiltersPage();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_load_current_page_if_controls_page_shown() throws Exception {
        mReportPageState.setControlsPageShown(true);

        presenter.init();

        verify(mFakeGetReportPageContentCase).execute(eq(CURRENT_PAGE_REQUEST), any(Subscriber.class));
        verify(mReportPageState).setCurrentPage(CURRENT_PAGE);
        verify(mView).showPage(PAGE_CONTENT);

        verify(mFakeGetReportMultiPagePropertyCase).execute(anyString(), any(Subscriber.class));
        verify(mFakeGetReportTotalPagesPropertyCase).execute(eq(REPORT_URI), any(Subscriber.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_perform_load_page_action() throws Exception {
        PageRequest _10page = new PageRequest.Builder()
                .setUri(REPORT_URI)
                .setRange("10")
                .asHtml()
                .build();

        presenter.loadPage("10");

        verify(mView).showPageLoader(true);
        verify(mFakeGetReportPageContentCase).execute(eq(_10page), any(Subscriber.class));
        verify(mReportPageState).setRequestedPage("10");
        verify(mReportPageState).setCurrentPage("10");
        verify(mView).showCurrentPage(10);
        verify(mView).showPage(PAGE_CONTENT);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_perform_run_report_action() throws Exception {
        presenter.runReport();

        verify(mFakeGetReportTotalPagesPropertyCase).execute(eq(REPORT_URI), any(Subscriber.class));

        verify(mView).showLoading();
        verify(mReportPageState).setCurrentPage("1");
        verify(mView).showCurrentPage(1);
        verify(mView).showPage(PAGE_CONTENT);
        verify(mView, times(3)).hideLoading(); // we are invoking 2 additional calls during run

        verify(mFakeGetReportMultiPagePropertyCase).execute(anyString(), any(Subscriber.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_perform_update_report_action() throws Exception {
        presenter.updateReport();

        verify(mFakeUpdateReportCase).execute(eq(REPORT_URI), any(Subscriber.class));
        verify(mView).resetPaginationControl();
        verify(mReportPageState).setCurrentPage("1");
        verify(mView).showCurrentPage(1);
        verify(mView).showPage(PAGE_CONTENT);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_perform_refresh_report_action() throws Exception {
        presenter.refresh();

        PageRequest _1page = new PageRequest.Builder()
                .setUri(REPORT_URI)
                .setRange("1")
                .asHtml()
                .build();

        verify(mView).showLoading();
        verify(mFakeReloadReportCase).execute(eq(_1page), any(Subscriber.class));
        verify(mView, times(3)).hideLoading(); // we are invoking 2 additional calls during refresh

        verify(mView).resetPaginationControl();
        verify(mReportPageState).setCurrentPage("1");
        verify(mView).showCurrentPage(1);
        verify(mView).showPage(PAGE_CONTENT);
    }

    @Test
    public void should_un_subscribe_from_use_cases_on_destroy_action() throws Exception {
        presenter.destroy();
        verify(mFakeGetReportShowControlsPropertyCase).unsubscribe();
        verify(mFakeGetReportMultiPagePropertyCase).unsubscribe();
        verify(mFakeGetReportTotalPagesPropertyCase).unsubscribe();
        verify(mFakeGetReportPageContentCase).unsubscribe();
        verify(mFakeRunReportCase).unsubscribe();
        verify(mFakeUpdateReportCase).unsubscribe();
        verify(mFakeReloadReportCase).unsubscribe();
        verify(mFakeFlushReportCachesCase).execute(REPORT_URI);
        verify(mFakeFlushInputControlsCase).execute(REPORT_URI);
    }

    @Test
    public void should_handle_not_empty_pages_case() throws Exception {
        mFakeGetReportTotalPagesPropertyCase.setPages(10);
        presenter.loadTotalPagesProperty();
        verify(mView).showTotalPages(10);
        verify(mView).setSaveActionVisibility(true);
        verify(mView).reloadMenu();
        verify(mFakeGetReportPageContentCase).execute(eq(CURRENT_PAGE_REQUEST), any(Subscriber.class));
    }

    @Test
    public void should_handle_empty_pages_case() throws Exception {
        mFakeGetReportTotalPagesPropertyCase.setPages(0);
        presenter.loadTotalPagesProperty();
        verify(mView).showEmptyPageMessage();
        verify(mView).setSaveActionVisibility(false);
        verify(mView).reloadMenu();
    }

    @Test
    public void should_toggle_pagination_control_if_multi_page_loaded() throws Exception {
        mFakeGetReportMultiPagePropertyCase.setIsMultiPage(false);
        presenter.loadMultiPageProperty();
        verify(mView).showPaginationControl(false);
    }

    @Test
    public void should_hide_error_showing_page() throws Exception {
        presenter.showPage("1", PAGE);
        verify(mView).hideError();
        verify(mReportPageState).setCurrentPage("1");
        verify(mView).showCurrentPage(1);
        verify(mView).showPage(PAGE_CONTENT);
    }

    @Test
    public void should_handle_report_execution_invalidation_case() throws Exception {
        ServiceException serviceException = new ServiceException("message", null, StatusCodes.REPORT_EXECUTION_INVALID);
        presenter.handleError(serviceException);
        verify(mFakeReloadReportCase).execute(eq(NEW_PAGE_REQUEST), any(Subscriber.class));
    }

    @Test
    public void should_handle_report_page_out_of_range_case() throws Exception {
        ServiceException serviceException = new ServiceException("message", null, StatusCodes.EXPORT_PAGE_OUT_OF_RANGE);
        presenter.handleError(serviceException);
        verify(mView).showPageOutOfRangeError();
        verify(mFakeGetReportPageContentCase).execute(eq(CURRENT_PAGE_REQUEST), any(Subscriber.class));
    }

    private void setupMocks() {
        ReportPageState state = new ReportPageState();
        state.setCurrentPage(CURRENT_PAGE);
        state.setRequestedPage(REQUESTED_PAGE);
        mReportPageState = spy(state);

        mFakeSaveScreenCaptureCase = spy(new FakeSaveScreenCaptureCase());
        mFakeGetReportShowControlsPropertyCase = spy(new FakeGetReportShowControlsPropertyCase());
        mFakeGetReportPageContentCase = spy(new FakeGetReportPageContentCase());
        mFakeGetReportMultiPagePropertyCase = spy(new FakeGetReportMultiPagePropertyCase());
        mFakeGetReportTotalPagesPropertyCase = spy(new FakeGetReportTotalPagesPropertyCase());
        mFakeRunReportCase = spy(new FakeRunReportCase());
        mFakeUpdateReportCase = spy(new FakeUpdateReportCase());
        mFakeReloadReportCase = spy(new FakeReloadReportCase());
        mFakeFlushReportCachesCase = spy(new FakeFlushReportCachesCase());
        mFakeFlushInputControlsCase = spy(new FakeFlushInputControlsCase());

        when(mView.getState()).thenReturn(mReportPageState);
    }

    private static class FakeGetReportTotalPagesPropertyCase extends GetReportTotalPagesPropertyCase {
        private int mPages;

        public FakeGetReportTotalPagesPropertyCase() {
            super(FakePreExecutionThread.create(), FakePostExecutionThread.create(), null, null);
        }

        public void setPages(int pages) {
            mPages = pages;
        }

        @Override
        protected Observable<Integer> buildUseCaseObservable(String reportUri) {
            return Observable.just(mPages);
        }
    }

    private static class FakeUpdateReportCase extends UpdateReportCase {
        public FakeUpdateReportCase() {
            super(FakePreExecutionThread.create(), FakePostExecutionThread.create(), null, null);
        }

        @Override
        protected Observable<ReportPage> buildUseCaseObservable(String reportUri) {
            return Observable.just(PAGE);
        }
    }

    private static class FakeRunReportCase extends RunReportCase {
        public FakeRunReportCase() {
            super(FakePreExecutionThread.create(), FakePostExecutionThread.create(), null, null);
        }

        @Override
        protected Observable<ReportPage> buildUseCaseObservable(String reportUri) {
            return Observable.just(PAGE);
        }
    }

    private static class FakeReloadReportCase extends ReloadReportCase {
        public FakeReloadReportCase() {
            super(FakePreExecutionThread.create(), FakePostExecutionThread.create(), null, null);
        }

        @Override
        protected Observable<ReportPage> buildUseCaseObservable(PageRequest request) {
            return Observable.just(PAGE);
        }
    }

    private static class FakeGetReportMultiPagePropertyCase extends GetReportMultiPagePropertyCase {
        private boolean mFakeResult;

        public FakeGetReportMultiPagePropertyCase() {
            super(FakePreExecutionThread.create(), FakePostExecutionThread.create(), null, null);
        }

        public void setIsMultiPage(boolean fakeResult) {
            mFakeResult = fakeResult;
        }

        @Override
        protected Observable<Boolean> buildUseCaseObservable(String reportUri) {
            return Observable.just(mFakeResult);
        }
    }

    private static class FakeGetReportPageContentCase extends GetReportPageContentCase {
        public FakeGetReportPageContentCase() {
            super(FakePreExecutionThread.create(), FakePostExecutionThread.create(), null, null);
        }

        @Override
        protected Observable<ReportPage> buildUseCaseObservable(@NonNull PageRequest request) {
            return Observable.just(PAGE);
        }
    }

    private class FakeSaveScreenCaptureCase extends SaveScreenCaptureCase {
        public FakeSaveScreenCaptureCase() {
            super(FakePreExecutionThread.create(), FakePostExecutionThread.create(), null);
        }

        @Override
        protected Observable<File> buildUseCaseObservable(ScreenCapture capture) {
            return Observable.just(
                    SCREEN_CAPTURED_FILE
            );
        }
    }
}