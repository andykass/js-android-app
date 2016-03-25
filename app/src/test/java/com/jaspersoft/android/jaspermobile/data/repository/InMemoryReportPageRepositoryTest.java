package com.jaspersoft.android.jaspermobile.data.repository;

import com.jaspersoft.android.jaspermobile.data.cache.report.ReportPageCache;
import com.jaspersoft.android.jaspermobile.data.repository.report.InMemoryReportPageRepository;
import com.jaspersoft.android.jaspermobile.data.repository.report.page.PageCreator;
import com.jaspersoft.android.jaspermobile.data.repository.report.page.PageCreatorFactory;
import com.jaspersoft.android.jaspermobile.domain.PageRequest;
import com.jaspersoft.android.jaspermobile.domain.ReportPage;
import com.jaspersoft.android.sdk.service.exception.ServiceException;
import com.jaspersoft.android.sdk.service.exception.StatusCodes;
import com.jaspersoft.android.sdk.service.report.ReportExecution;
import com.jaspersoft.android.sdk.service.rx.report.RxReportExecution;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;

import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class InMemoryReportPageRepositoryTest {

    private static final String REPORT_URI = "/my/uri";
    private static final String PAGE_POSITION = "100";
    private static final PageRequest PAGE_REQUEST = new PageRequest.Builder()
            .setUri(REPORT_URI)
            .setRange(PAGE_POSITION)
            .asHtml()
            .build();

    private static final byte[] CONTENT = "page".getBytes();
    private static final ReportPage ANY_PAGE = new ReportPage(CONTENT, true);

    @Mock
    ReportPageCache mReportPageCache;
    @Mock
    RxReportExecution mRxReportExecution;
    @Mock
    ReportExecution mReportExecution;
    @Mock
    PageCreatorFactory mPageCreatorFactory;
    @Mock
    PageCreator mPageCreator;

    private InMemoryReportPageRepository mInMemoryReportPageRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        setupMocks();
        mInMemoryReportPageRepository = new InMemoryReportPageRepository(
                mPageCreatorFactory,
                mReportPageCache
        );
    }

    @Test
    public void should_get_page_from_network_if_cache_empty() throws Exception {
        givenEmptyReportPageCache();

        whenReportPageRequest();

        thenShouldProvidePageCreator();
        thenShouldCreatePage();
    }

    @Test
    public void should_get_page_from_cache_if_one_not_empty() throws Exception {
        givenNotEmptyReportPageCache();

        whenReportPageRequest();

        thenShouldReturnPageFromCache();
    }

    @Test
    public void should_return_empty_page_if_encountered_error() throws Exception {
        givenReportPageCreatorThrowsExportFailedError();

        whenReportPageRequest();

        thenShouldSaveEmptyPageToCache();
    }

    private void thenShouldProvidePageCreator() {
        verify(mPageCreatorFactory).create(PAGE_REQUEST, mReportExecution);
    }

    private void thenShouldCreatePage() throws Exception {
        verify(mPageCreator).create();
    }

    private void givenEmptyReportPageCache() {
        when(mReportPageCache.get(any(PageRequest.class))).thenReturn(null);
    }

    private void givenReportPageCreatorThrowsExportFailedError() throws Exception {
        when(mPageCreator.create()).thenThrow(new ServiceException(null, null, StatusCodes.EXPORT_EXECUTION_FAILED));
    }

    private void thenShouldSaveEmptyPageToCache() {
        verify(mReportPageCache).put(PAGE_REQUEST, ReportPage.EMPTY);
    }

    private void givenNotEmptyReportPageCache() {
        when(mReportPageCache.get(any(PageRequest.class))).thenReturn(ANY_PAGE);
    }

    private void thenShouldReturnPageFromCache() {
        verify(mReportPageCache).get(PAGE_REQUEST);
    }

    private void whenReportPageRequest() {
        TestSubscriber<ReportPage> test = new TestSubscriber<>();
        mInMemoryReportPageRepository.get(mRxReportExecution, PAGE_REQUEST).subscribe(test);
        test.assertNoErrors();
    }

    private void setupMocks() throws IOException {
        when(mPageCreatorFactory.create(any(PageRequest.class), any(ReportExecution.class))).thenReturn(mPageCreator);
        when(mRxReportExecution.toBlocking()).thenReturn(mReportExecution);
    }
}