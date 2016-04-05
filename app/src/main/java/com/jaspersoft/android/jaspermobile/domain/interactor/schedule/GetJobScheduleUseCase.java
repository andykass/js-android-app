package com.jaspersoft.android.jaspermobile.domain.interactor.schedule;

import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.executor.PreExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.interactor.AbstractUseCase2;
import com.jaspersoft.android.jaspermobile.domain.repository.schedule.ScheduleRepository;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.sdk.service.data.schedule.JobForm;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func0;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@PerScreen
public class GetJobScheduleUseCase extends AbstractUseCase2<JobForm, Integer> {
    private ScheduleRepository mScheduleRepository;

    @Inject
    public GetJobScheduleUseCase(
            PreExecutionThread preExecutionThread,
            PostExecutionThread postExecutionThread,
            ScheduleRepository scheduleRepository
    ) {
        super(preExecutionThread, postExecutionThread);
        mScheduleRepository = scheduleRepository;
    }

    @Override
    protected Observable<JobForm> buildUseCaseObservable(final Integer jobId) {
        return Observable.defer(new Func0<Observable<JobForm>>() {
            @Override
            public Observable<JobForm> call() {
                try {
                    JobForm result = mScheduleRepository.readForm(jobId);
                    return Observable.just(result);
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        }).cache();
    }
}
