package com.jaspersoft.android.jaspermobile.presentation.presenter;

import android.webkit.ConsoleMessage;

import com.jaspersoft.android.jaspermobile.FakePostExecutionThread;
import com.jaspersoft.android.jaspermobile.FakePreExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.ScreenCapture;
import com.jaspersoft.android.jaspermobile.domain.VisualizeTemplate;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetVisualizeExecOptionsCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.report.GetVisualizeTemplateCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.resource.SaveScreenCaptureCase;
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
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.VisualizeEvents;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.VisualizeExecOptions;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.VisualizeViewModel;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.WebViewErrorEvent;
import com.jaspersoft.android.jaspermobile.presentation.model.visualize.WebViewEvents;
import com.jaspersoft.android.jaspermobile.presentation.page.ReportPageState;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class ReportVisualizePresenterTest {

    private static final VisualizeTemplate VIS_TEMPLATE = new VisualizeTemplate("content", "url");
    private static final double SCREEN_DIAGONAL = 10.1;
    private static final String JSON_REPORT_PARAMS = "{}";
    private static final String REPORT_URI = "/my/uri";
    private static final File SCREEN_CAPTURED_FILE = new File("my_file.jpg");

    private ReportVisualizePresenter mReportVisualizePresenter;
    private FakeGetVisualizeTemplateCase mGetVisualizeTemplateCase;
    private FakeGetReportShowControlsPropertyCase mGetReportShowControlsPropertyCase;
    private FakeGetVisualizeExecOptionsCase mGetJsonParamsCase;
    private FakeFlushInputControlsCase mFakeFlushInputControlsCase;
    private FakeGetReportMetadataCase mFakeGetReportMetadataCase;
    private FakeAuthorizeSessionUseCase mFakeAuthorizeSessionUseCase;
    private FakeSaveScreenCaptureCase mFakeSaveScreenCaptureCase;

    @Mock
    RequestExceptionHandler mExceptionHandler;

    @Mock
    VisualizeReportContract.View mView;
    @Mock
    VisualizeViewModel mVisualizeViewModel;
    @Mock
    VisualizeEvents mVisualizeEvents;
    @Mock
    WebViewEvents mWebViewEvents;

    @Mock
    ResourceLookup mReportResource;

    private ReportPageState fakeState;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        setUpMocks();

        mReportVisualizePresenter = new ReportVisualizePresenter(
                SCREEN_DIAGONAL,
                REPORT_URI,
                FakePostExecutionThread.create(),
                mExceptionHandler,
                mGetReportShowControlsPropertyCase,
                mGetVisualizeTemplateCase,
                mGetJsonParamsCase,
                mFakeFlushInputControlsCase,
                mFakeGetReportMetadataCase,
                mFakeAuthorizeSessionUseCase,
                mFakeSaveScreenCaptureCase);
        mReportVisualizePresenter.injectView(mView);
    }

    @Test
    public void should_handle_screen_capture_error() throws Exception {
        when(mExceptionHandler.extractMessage(any(Throwable.class))).thenReturn("Opps!");
        when(mFakeSaveScreenCaptureCase.buildUseCaseObservable(any(ScreenCapture.class)))
                .thenReturn(Observable.<File>error(new IOException()));

        mReportVisualizePresenter.shareReport(null);
        verify(mView).showError("Opps!");
    }

    @Test
    public void should_handle_screen_capture_success() throws Exception {
        mReportVisualizePresenter.shareReport(null);
        verify(mView).navigateToAnnotationPage(SCREEN_CAPTURED_FILE);
    }

    @Test
    public void should_request_input_controls_on_second_run() throws Exception {
        fakeState.setControlsPageShown(false);

        mReportVisualizePresenter.init();

        verify(mView).showLoading();
        verify(mGetReportShowControlsPropertyCase).execute(eq(REPORT_URI), any(Subscriber.class));
    }

    @Test
    public void should_display_controls_page_if_report_requires_controls() throws Exception {
        fakeState.setControlsPageShown(false);
        mGetReportShowControlsPropertyCase.setNeedParams(true);

        mReportVisualizePresenter.init();

        verify(fakeState).setControlsPageShown(true);
        verify(mView).showFilterAction(true);
        verify(mView).showInitialFiltersPage();
    }

    @Test
    public void should_load_template_if_report_not_requires_controls() throws Exception {
        when(fakeState.isControlsPageShown()).thenReturn(false);
        mGetReportShowControlsPropertyCase.setNeedPrompt(false);

        mReportVisualizePresenter.init();

        double scale = SCREEN_DIAGONAL / 10.1;
        Map<String, Double> clientParams = Collections.singletonMap("initial_scale", scale);
        verify(mGetVisualizeTemplateCase).execute(eq(clientParams), any(Subscriber.class));

        verify(fakeState).setControlsPageShown(true);
        verify(mView).showFilterAction(false);
        verify(mView).loadTemplateInView(VIS_TEMPLATE);
    }

    @Test
    public void on_resume_should_subscribe_to_visualize_start_load_event() throws Exception {
        when(mVisualizeEvents.loadStartEvent()).thenReturn(Observable.<Void>just(null));

        mReportVisualizePresenter.resume();

        verify(mView).showLoading();
        verify(mView).showWebView(false);
        verify(mView).resetPaginationControl();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void on_resume_should_subscribe_to_visualize_script_loaded_event() throws Exception {
        when(mVisualizeEvents.scriptLoadedEvent()).thenReturn(Observable.<Void>just(null));

        mReportVisualizePresenter.resume();

        verify(mGetJsonParamsCase).execute(eq(REPORT_URI), any(Subscriber.class));
    }

    @Test
    public void on_resume_should_subscribe_to_visualize_complete_load_event() throws Exception {
        when(mVisualizeEvents.loadCompleteEvent()).thenReturn(Observable.just(new LoadCompleteEvent("")));

        mReportVisualizePresenter.resume();

        verify(mView).showWebView(true);
    }

    @Test
    public void on_resume_should_subscribe_to_visualize_error_load_event() throws Exception {
        when(mVisualizeEvents.loadErrorEvent()).thenReturn(Observable.just(new ErrorEvent("error")));

        mReportVisualizePresenter.resume();

        verify(mView).showError("error");
    }

    @Test
    public void on_resume_should_subscribe_to_report_complete_event_with_zero_page() throws Exception {
        when(mVisualizeEvents.reportCompleteEvent()).thenReturn(Observable.just(new ReportCompleteEvent(0)));

        mReportVisualizePresenter.resume();

        verify(mView).showSaveAction(false);
        verify(mView).showPagination(false);
        verify(mView).showEmptyPageMessage();
    }

    @Test
    public void on_resume_should_subscribe_to_report_complete_event_with_single_page() throws Exception {
        when(mVisualizeEvents.reportCompleteEvent()).thenReturn(Observable.just(new ReportCompleteEvent(1)));

        mReportVisualizePresenter.resume();

        verify(mView).hideEmptyPageMessage();
        verify(mView).showSaveAction(true);
        verify(mView).showPagination(false);
    }

    @Test
    public void on_resume_should_subscribe_to_report_complete_event_with_multi_page() throws Exception {
        when(mVisualizeEvents.reportCompleteEvent()).thenReturn(Observable.just(new ReportCompleteEvent(2)));

        mReportVisualizePresenter.resume();

        verify(mView).hideEmptyPageMessage();
        verify(mView).showSaveAction(true);
        verify(mView).setPaginationTotalPages(2);
        verify(mView).showPagination(true);
    }

    @Test
    public void on_resume_should_subscribe_to_page_load_complete_event() throws Exception {
        when(mVisualizeEvents.pageLoadCompleteEvent()).thenReturn(Observable.just(new PageLoadCompleteEvent(2)));

        mReportVisualizePresenter.resume();

        verify(mView).setPaginationEnabled(true);
        verify(mView).setPaginationCurrentPage(2);
    }

    @Test
    public void on_resume_should_subscribe_to_page_load_error_event() throws Exception {
        when(mVisualizeEvents.pageLoadErrorEvent()).thenReturn(Observable.just(new PageLoadErrorEvent("error", 2)));

        mReportVisualizePresenter.resume();

        verify(mView).setPaginationEnabled(true);
        verify(mView).setPaginationCurrentPage(2);
        verify(mView).showError("error");
    }

    @Test
    public void on_resume_should_subscribe_to_multipage_event1() throws Exception {
        when(mVisualizeEvents.multiPageLoadEvent()).thenReturn(Observable.just(new MultiPageLoadEvent(true)));

        mReportVisualizePresenter.resume();

        verify(mView).showPagination(false);
    }

    @Test
    public void on_resume_should_subscribe_to_multipage_event2() throws Exception {
        when(mView.getPaginationTotalPages()).thenReturn(2);
        when(mVisualizeEvents.multiPageLoadEvent()).thenReturn(Observable.just(new MultiPageLoadEvent(true)));

        mReportVisualizePresenter.resume();

        verify(mView).showPagination(true);
    }

    @Test
    public void on_resume_should_subscribe_to_external_click_event() throws Exception {
        when(mVisualizeEvents.externalReferenceClickEvent()).thenReturn(Observable.just(new ExternalReferenceClickEvent("link")));

        mReportVisualizePresenter.resume();

        verify(mView).showExternalLink("link");
    }

    @Test
    public void on_resume_should_subscribe_to_execution_event() throws Exception {
        mFakeGetReportMetadataCase.setResource(mReportResource);

        ExecutionReferenceClickEvent event = new ExecutionReferenceClickEvent("{}");
        when(mVisualizeEvents.executionReferenceClickEvent()).thenReturn(Observable.just(event));

        mReportVisualizePresenter.resume();

        verify(mView).showLoading();
        verify(mFakeGetReportMetadataCase).execute(eq("{}"), any(Subscriber.class));
        verify(mView).executeReport(mReportResource);
    }

    @Test
    public void on_resume_should_subscribe_to_window_error_event() throws Exception {
        when(mVisualizeEvents.windowErrorEvent()).thenReturn(Observable.just(new ErrorEvent("error")));

        mReportVisualizePresenter.resume();

        verify(mView).showError("error");
    }

    @Test
    public void on_resume_should_subscribe_to_webview_on_progress_event() throws Exception {
        when(mWebViewEvents.progressChangedEvent()).thenReturn(Observable.just(10));

        mReportVisualizePresenter.resume();

        verify(mView).updateDeterminateProgress(10);
    }

    @Test
    public void on_resume_should_subscribe_to_webview_received_error_event() throws Exception {
        when(mWebViewEvents.receivedErrorEvent()).thenReturn(Observable.just(new WebViewErrorEvent("title", "message")));

        mReportVisualizePresenter.resume();

        verify(mView).showWebView(false);
        verify(mView).showError("title" + "\n" + "message");
    }

    @Test
    public void on_resume_should_subscribe_to_session_expired_event() throws Exception {
        when(mWebViewEvents.sessionExpiredEvent()).thenReturn(Observable.<Void>just(null));

        mReportVisualizePresenter.resume();

        verify(mView).handleSessionExpiration();
    }

    @Test
    public void load_report_page_should_delegate_call_visualize_component() throws Exception {
        mReportVisualizePresenter.loadPage("1");
        mView.getVisualize().loadPage("1");
    }

    @Test
    public void update_report_page_should_delegate_receive_json_params() throws Exception {
        mReportVisualizePresenter.updateReport();

        verify(mGetJsonParamsCase).execute(eq(REPORT_URI), any(Subscriber.class));
        verify(mView).resetZoom();
        verify(mView.getVisualize()).update(JSON_REPORT_PARAMS);
    }

    @Test
    public void refresh_report_page_should_delegate_refresh_call() throws Exception {
        mReportVisualizePresenter.refresh();

        verify(mView.getVisualize()).refresh();
        verify(mView).resetZoom();
        verify(mView).showWebView(false);
        verify(mView).showPagination(false);
        verify(mView).resetPaginationControl();
        verify(mView).showLoading();
    }

    @Test
    public void should_unsubscribe_from_use_cases_on_destroy() throws Exception {
        mReportVisualizePresenter.destroy();
        verify(mGetReportShowControlsPropertyCase).unsubscribe();
        verify(mGetVisualizeTemplateCase).unsubscribe();
        verify(mGetJsonParamsCase).unsubscribe();
        verify(mFakeFlushInputControlsCase).execute(REPORT_URI);
    }

    private void setUpMocks() {
        fakeState = spy(new ReportPageState());
        mGetVisualizeTemplateCase = spy(new FakeGetVisualizeTemplateCase());
        mGetReportShowControlsPropertyCase = spy(new FakeGetReportShowControlsPropertyCase());
        mGetJsonParamsCase = spy(new FakeGetVisualizeExecOptionsCase());
        mFakeFlushInputControlsCase = spy(new FakeFlushInputControlsCase());
        mFakeGetReportMetadataCase = spy(new FakeGetReportMetadataCase());
        mFakeAuthorizeSessionUseCase = spy(new FakeAuthorizeSessionUseCase());
        mFakeSaveScreenCaptureCase = spy(new FakeSaveScreenCaptureCase());

        fakeState.setControlsPageShown(false);
        when(mView.getState()).thenReturn(fakeState);
        when(mView.getVisualize()).thenReturn(mVisualizeViewModel);
        when(mView.getPaginationTotalPages()).thenReturn(0);

        when(mVisualizeViewModel.visualizeEvents()).thenReturn(mVisualizeEvents);
        when(mVisualizeViewModel.webViewEvents()).thenReturn(mWebViewEvents);

        when(mWebViewEvents.progressChangedEvent()).thenReturn(Observable.<Integer>empty());
        when(mWebViewEvents.receivedErrorEvent()).thenReturn(Observable.<WebViewErrorEvent>empty());
        when(mWebViewEvents.sessionExpiredEvent()).thenReturn(Observable.<Void>empty());
        when(mWebViewEvents.consoleMessageEvent()).thenReturn(Observable.<ConsoleMessage>empty());

        when(mVisualizeViewModel.run(any(VisualizeExecOptions.class))).thenReturn(mVisualizeViewModel);
        when(mVisualizeViewModel.refresh()).thenReturn(mVisualizeViewModel);
        when(mVisualizeViewModel.loadPage(anyString())).thenReturn(mVisualizeViewModel);
        when(mVisualizeViewModel.update(anyString())).thenReturn(mVisualizeViewModel);

        when(mVisualizeEvents.loadStartEvent()).thenReturn(Observable.<Void>empty());
        when(mVisualizeEvents.scriptLoadedEvent()).thenReturn(Observable.<Void>empty());
        when(mVisualizeEvents.loadCompleteEvent()).thenReturn(Observable.<LoadCompleteEvent>empty());
        when(mVisualizeEvents.loadErrorEvent()).thenReturn(Observable.<ErrorEvent>empty());
        when(mVisualizeEvents.reportCompleteEvent()).thenReturn(Observable.<ReportCompleteEvent>empty());
        when(mVisualizeEvents.pageLoadCompleteEvent()).thenReturn(Observable.<PageLoadCompleteEvent>empty());
        when(mVisualizeEvents.pageLoadErrorEvent()).thenReturn(Observable.<PageLoadErrorEvent>empty());
        when(mVisualizeEvents.multiPageLoadEvent()).thenReturn(Observable.<MultiPageLoadEvent>empty());
        when(mVisualizeEvents.externalReferenceClickEvent()).thenReturn(Observable.<ExternalReferenceClickEvent>empty());
        when(mVisualizeEvents.executionReferenceClickEvent()).thenReturn(Observable.<ExecutionReferenceClickEvent>empty());
        when(mVisualizeEvents.windowErrorEvent()).thenReturn(Observable.<ErrorEvent>empty());
        when(mVisualizeEvents.authErrorEvent()).thenReturn(Observable.<ErrorEvent>empty());
    }

    private static class FakeGetVisualizeTemplateCase extends GetVisualizeTemplateCase {
        public FakeGetVisualizeTemplateCase() {
            super(FakePreExecutionThread.create(), FakePostExecutionThread.create(), null, null);
        }

        @Override
        protected Observable<VisualizeTemplate> buildUseCaseObservable(Map<String, ?> params) {
            return Observable.just(VIS_TEMPLATE);
        }
    }

    private class FakeGetVisualizeExecOptionsCase extends GetVisualizeExecOptionsCase {
        public FakeGetVisualizeExecOptionsCase() {
            super(FakePreExecutionThread.create(), FakePostExecutionThread.create(), null, null, null, null);
        }

        @Override
        protected Observable<VisualizeExecOptions.Builder> buildUseCaseObservable(String reportUri) {
            return Observable.just(
                    new VisualizeExecOptions.Builder().setParams(JSON_REPORT_PARAMS)
            );
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