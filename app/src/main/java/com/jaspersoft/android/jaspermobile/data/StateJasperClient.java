package com.jaspersoft.android.jaspermobile.data;

import com.jaspersoft.android.jaspermobile.data.cache.profile.JasperServerCache;
import com.jaspersoft.android.jaspermobile.domain.JasperServer;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.sdk.service.filter.FiltersService;
import com.jaspersoft.android.sdk.service.report.ReportService;
import com.jaspersoft.android.sdk.service.report.schedule.ReportScheduleService;
import com.jaspersoft.android.sdk.service.repository.RepositoryService;
import com.jaspersoft.android.sdk.service.rx.filter.RxFiltersService;
import com.jaspersoft.android.sdk.service.rx.report.RxReportService;
import com.jaspersoft.android.sdk.service.rx.report.schedule.RxReportScheduleService;
import com.jaspersoft.android.sdk.service.rx.repository.RxRepositoryService;

import rx.Observable;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class StateJasperClient implements JasperRestClient {
    private final Profile mProfile;
    private final JasperServerCache mServerCache;

    private final JasperRestClient mFakeClient;
    private final JasperRestClient mRealClient;

    public StateJasperClient(
            Profile profile,
            JasperServerCache serverCache,
            JasperRestClient realClient,
            JasperRestClient fakeClient
    ) {
        mProfile = profile;
        mServerCache = serverCache;
        mRealClient = realClient;
        mFakeClient = fakeClient;
    }

    @Override
    public ReportService syncReportService() {
        return getDelegate().syncReportService();
    }

    @Override
    public FiltersService syncFilterService() {
        return getDelegate().syncFilterService();
    }

    @Override
    public RepositoryService syncRepositoryService() {
        return getDelegate().syncRepositoryService();
    }

    @Override
    public ReportScheduleService syncScheduleService() {
        return getDelegate().syncScheduleService();
    }

    @Override
    public Observable<RxReportService> reportService() {
        return getDelegate().reportService();
    }

    @Override
    public Observable<RxRepositoryService> repositoryService() {
        return getDelegate().repositoryService();
    }

    @Override
    public Observable<RxFiltersService> filtersService() {
        return getDelegate().filtersService();
    }

    @Override
    public Observable<RxReportScheduleService> scheduleService() {
        return getDelegate().scheduleService();
    }

    private JasperRestClient getDelegate() {
        JasperServer jasperServer = providesJasperServer();
        return jasperServer.isFake() ? mFakeClient : mRealClient;
    }

    private JasperServer providesJasperServer() {
        return mServerCache.get(mProfile);
    }
}
