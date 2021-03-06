package com.jaspersoft.android.jaspermobile.data.repository.report;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.data.JasperRestClient;
import com.jaspersoft.android.jaspermobile.data.entity.mapper.InputControlsMapper;
import com.jaspersoft.android.jaspermobile.data.entity.mapper.ReportParamsMapper;
import com.jaspersoft.android.jaspermobile.domain.repository.report.ReportOptionsRepository;
import com.jaspersoft.android.jaspermobile.internal.di.PerProfile;
import com.jaspersoft.android.sdk.client.oxm.control.InputControlState;
import com.jaspersoft.android.sdk.client.oxm.report.ReportParameter;
import com.jaspersoft.android.sdk.service.data.report.option.ReportOption;
import com.jaspersoft.android.sdk.service.rx.filter.RxFiltersService;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerProfile
public final class InMemoryReportOptionsRepository implements ReportOptionsRepository {

    private final ReportParamsMapper mReportParamsMapper;
    private final InputControlsMapper mControlsMapper;
    private final JasperRestClient mJasperRestClient;

    @Inject
    public InMemoryReportOptionsRepository(
            JasperRestClient jasperRestClient,
            ReportParamsMapper reportParamsMapper,
            InputControlsMapper controlsMapper
    ) {
        mJasperRestClient = jasperRestClient;
        mReportParamsMapper = reportParamsMapper;
        mControlsMapper = controlsMapper;
    }

    @NonNull
    @Override
    public Observable<Set<ReportOption>> getReportOption(@NonNull final String reportUri) {
        return Observable.defer(new Func0<Observable<Set<ReportOption>>>() {
            @Override
            public Observable<Set<ReportOption>> call() {
                return mJasperRestClient.filtersService().flatMap(new Func1<RxFiltersService, Observable<Set<ReportOption>>>() {
                    @Override
                    public Observable<Set<ReportOption>> call(RxFiltersService service) {
                        return service.listReportOptions(reportUri);
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public Observable<ReportOption> createReportOptionWithOverride(@NonNull final String reportUri,
                                                                   @NonNull final String label,
                                                                   @NonNull final List<ReportParameter> params) {
        return Observable.defer(new Func0<Observable<ReportOption>>() {
            @Override
            public Observable<ReportOption> call() {
                final List<com.jaspersoft.android.sdk.network.entity.report.ReportParameter> reportParameters =
                        mReportParamsMapper.legacyParamsToRetrofitted(params);
                return mJasperRestClient.filtersService().flatMap(new Func1<RxFiltersService, Observable<ReportOption>>() {
                    @Override
                    public Observable<ReportOption> call(RxFiltersService service) {
                        return service.createReportOption(reportUri, label, reportParameters, true);
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public Observable<Void> deleteReportOption(@NonNull final String uri, @NonNull final String optionId) {
        return Observable.defer(new Func0<Observable<Void>>() {
            @Override
            public Observable<Void> call() {
                return mJasperRestClient.filtersService().flatMap(new Func1<RxFiltersService, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(RxFiltersService service) {
                        return service.deleteReportOption(uri, optionId);
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public Observable<List<InputControlState>> getReportOptionStates(@NonNull final String reportUri) {
        return Observable.defer(new Func0<Observable<List<com.jaspersoft.android.sdk.network.entity.control.InputControlState>>>() {
            @Override
            public Observable<List<com.jaspersoft.android.sdk.network.entity.control.InputControlState>> call() {
                return mJasperRestClient.filtersService().flatMap(new Func1<RxFiltersService, Observable<List<com.jaspersoft.android.sdk.network.entity.control.InputControlState>>>() {
                    @Override
                    public Observable<List<com.jaspersoft.android.sdk.network.entity.control.InputControlState>> call(RxFiltersService service) {
                        return service.listResourceStates(reportUri, true);
                    }
                });
            }
        }).map(new Func1<List<com.jaspersoft.android.sdk.network.entity.control.InputControlState>, List<InputControlState>>() {
            @Override
            public List<InputControlState> call(List<com.jaspersoft.android.sdk.network.entity.control.InputControlState> inputControlStates) {
                return mControlsMapper.retrofittedStatesToLegacy(inputControlStates);
            }
        });
    }
}
