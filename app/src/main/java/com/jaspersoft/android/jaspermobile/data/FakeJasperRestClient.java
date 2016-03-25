package com.jaspersoft.android.jaspermobile.data;

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
public class FakeJasperRestClient implements JasperRestClient {

    public FakeJasperRestClient() {
    }

    @Override
    public ReportService syncReportService() {
        return null;
    }

    @Override
    public FiltersService syncFilterService() {
        return null;
    }

    @Override
    public RepositoryService syncRepositoryService() {
        return null;
    }

    @Override
    public ReportScheduleService syncScheduleService() {
        return null;
    }

    @Override
    public Observable<RxReportService> reportService() {
        return Observable.empty();
    }

    @Override
    public Observable<RxRepositoryService> repositoryService() {
        return Observable.empty();
    }

    @Override
    public Observable<RxFiltersService> filtersService() {
        return Observable.empty();
    }

    @Override
    public Observable<RxReportScheduleService> scheduleService() {
        return Observable.empty();
    }
}
