package com.jaspersoft.android.jaspermobile.domain.interactor.schedule;

import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.executor.PreExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.interactor.AbstractUseCase2;
import com.jaspersoft.android.jaspermobile.domain.repository.schedule.ScheduleRepository;
import com.jaspersoft.android.jaspermobile.domain.request.UpdateJobRequest;
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
public class UpdateJobScheduleUseCase extends AbstractUseCase2<JobForm, UpdateJobRequest> {
    private ScheduleRepository mScheduleRepository;

    @Inject
    public UpdateJobScheduleUseCase(
            PreExecutionThread preExecutionThread,
            PostExecutionThread postExecutionThread,
            ScheduleRepository scheduleRepository
    ) {
        super(preExecutionThread, postExecutionThread);
        mScheduleRepository = scheduleRepository;
    }

    @Override
    protected Observable<JobForm> buildUseCaseObservable(final UpdateJobRequest request) {
        return Observable.defer(new Func0<Observable<JobForm>>() {
            @Override
            public Observable<JobForm> call() {
                try {
                    JobForm result = mScheduleRepository.updateForm(request.getJobId(), request.getJobForm());
                    return Observable.just(result);
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        }).cache();
    }
}
