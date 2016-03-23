package com.jaspersoft.android.jaspermobile.domain.interactor.report;

import com.jaspersoft.android.jaspermobile.data.cache.profile.CredentialsCache;
import com.jaspersoft.android.jaspermobile.data.cache.report.ReportParamsCache;
import com.jaspersoft.android.jaspermobile.data.entity.mapper.ReportParamsMapper;
import com.jaspersoft.android.jaspermobile.domain.AppCredentials;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.executor.PreExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.interactor.AbstractUseCase;
import com.jaspersoft.android.jaspermobile.internal.di.PerProfile;
import com.jaspersoft.android.jaspermobile.ui.model.visualize.VisualizeExecOptions;
import com.jaspersoft.android.sdk.client.oxm.report.ReportParameter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func0;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerProfile
public class GetVisualizeExecOptionsCase extends AbstractUseCase<VisualizeExecOptions.Builder, String> {

    private final Profile mProfile;
    private final CredentialsCache mCredentialsCache;
    private final ReportParamsCache mReportParamsCache;
    private final ReportParamsMapper mReportParamsMapper;

    @Inject
    public GetVisualizeExecOptionsCase(PreExecutionThread preExecutionThread,
                                       PostExecutionThread postExecutionThread,
                                       Profile profile,
                                       CredentialsCache credentialsCache,
                                       ReportParamsCache reportParamsCache,
                                       ReportParamsMapper reportParamsMapper
    ) {
        super(preExecutionThread, postExecutionThread);
        mProfile = profile;
        mCredentialsCache = credentialsCache;
        mReportParamsCache = reportParamsCache;
        mReportParamsMapper = reportParamsMapper;
    }

    @Override
    protected Observable<VisualizeExecOptions.Builder> buildUseCaseObservable(@NotNull final String reportUri) {
        return Observable.defer(new Func0<Observable<VisualizeExecOptions.Builder>>() {
            @Override
            public Observable<VisualizeExecOptions.Builder> call() {
                AppCredentials credentials = mCredentialsCache.get(mProfile);
                List<ReportParameter> reportParameters = mReportParamsCache.get(reportUri);
                String jsonParams = mReportParamsMapper.legacyParamsToJson(reportParameters);
                VisualizeExecOptions.Builder builder = new VisualizeExecOptions.Builder()
                        .setUri(reportUri)
                        .setAppCredentials(credentials)
                        .setParams(jsonParams);
                return Observable.just(builder);
            }
        });
    }
}
