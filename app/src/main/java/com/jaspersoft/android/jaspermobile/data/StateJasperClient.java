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
    private JasperRestClient mDelegate;

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
        updateState();
    }

    @Override
    public ReportService syncReportService() {
        updateState();
        return mDelegate.syncReportService();
    }

    @Override
    public FiltersService syncFilterService() {
        updateState();
        return mDelegate.syncFilterService();
    }

    @Override
    public RepositoryService syncRepositoryService() {
        updateState();
        return mDelegate.syncRepositoryService();
    }

    @Override
    public ReportScheduleService syncScheduleService() {
        updateState();
        return mDelegate.syncScheduleService();
    }

    @Override
    public Observable<RxReportService> reportService() {
        updateState();
        return mDelegate.reportService();
    }

    @Override
    public Observable<RxRepositoryService> repositoryService() {
        updateState();
        return mDelegate.repositoryService();
    }

    @Override
    public Observable<RxFiltersService> filtersService() {
        updateState();
        return mDelegate.filtersService();
    }

    @Override
    public Observable<RxReportScheduleService> scheduleService() {
        updateState();
        return mDelegate.scheduleService();
    }

    private void updateState() {
        JasperServer jasperServer = providesJasperServer();
        JasperRestClient delegate = jasperServer.isFake() ? mFakeClient : mRealClient;
        setDelegate(delegate);
    }

    private void setDelegate(JasperRestClient delegate) {
        mDelegate = delegate;
    }

    private JasperServer providesJasperServer() {
        return mServerCache.get(mProfile);
    }
}
