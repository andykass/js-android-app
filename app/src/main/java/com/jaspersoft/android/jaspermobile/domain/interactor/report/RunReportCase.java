package com.jaspersoft.android.jaspermobile.domain.interactor.report;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.domain.Report;
import com.jaspersoft.android.jaspermobile.domain.ReportPage;
import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.executor.PreExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.interactor.AbstractUseCase;
import com.jaspersoft.android.jaspermobile.domain.repository.report.ReportPageRepository;
import com.jaspersoft.android.jaspermobile.domain.repository.report.ReportRepository;
import com.jaspersoft.android.jaspermobile.internal.di.PerReport;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerReport
public class RunReportCase extends AbstractUseCase<ReportPage, String> {

    private final ReportRepository mReportRepository;
    private final ReportPageRepository mReportPageRepository;

    @Inject
    public RunReportCase(PreExecutionThread preExecutionThread,
                         PostExecutionThread postExecutionThread,
                         ReportRepository reportRepository,
                         ReportPageRepository reportPageRepository
    ) {
        super(preExecutionThread, postExecutionThread);
        mReportRepository = reportRepository;
        mReportPageRepository = reportPageRepository;
    }

    @Override
    protected Observable<ReportPage> buildUseCaseObservable(@NonNull final String reportUri) {
        return mReportRepository.getReport(reportUri)
                .flatMap(new Func1<Report, Observable<ReportPage>>() {
                    @Override
                    public Observable<ReportPage> call(Report report) {
                        return mReportPageRepository.get(report, "1");
                    }
                });
    }
}
