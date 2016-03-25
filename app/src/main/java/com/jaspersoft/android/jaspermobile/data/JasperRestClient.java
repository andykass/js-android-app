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
public interface JasperRestClient {
    ReportService syncReportService();

    FiltersService syncFilterService();

    RepositoryService syncRepositoryService();

    ReportScheduleService syncScheduleService();

    Observable<RxReportService> reportService();

    Observable<RxRepositoryService> repositoryService();

    Observable<RxFiltersService> filtersService();

    Observable<RxReportScheduleService> scheduleService();
}
