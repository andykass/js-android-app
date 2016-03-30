package com.jaspersoft.android.jaspermobile.domain.interactor.schedule;

import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.executor.PreExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.interactor.AbstractUseCase2;
import com.jaspersoft.android.jaspermobile.domain.repository.schedule.ScheduleRepository;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.sdk.service.data.schedule.JobData;
import com.jaspersoft.android.sdk.service.data.schedule.JobForm;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func0;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@PerScreen
public class SaveJobScheduleUseCase extends AbstractUseCase2<JobData, JobForm> {
    private ScheduleRepository mScheduleRepository;

    @Inject
    public SaveJobScheduleUseCase(
            PreExecutionThread preExecutionThread,
            PostExecutionThread postExecutionThread,
            ScheduleRepository scheduleRepository
    ) {
        super(preExecutionThread, postExecutionThread);
        mScheduleRepository = scheduleRepository;
    }

    @Override
    protected Observable<JobData> buildUseCaseObservable(final JobForm form) {
        return Observable.defer(new Func0<Observable<JobData>>() {
            @Override
            public Observable<JobData> call() {
                try {
                    JobData result = mScheduleRepository.createForm(form);
                    return Observable.just(result);
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        }).cache();
    }
}
