package com.jaspersoft.android.jaspermobile.domain.interactor.schedule;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleBundle;
import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.executor.PreExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.interactor.AbstractUseCase2;
import com.jaspersoft.android.jaspermobile.domain.repository.schedule.ScheduleRepository;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func0;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@PerScreen
public class UpdateJobScheduleUseCase extends AbstractUseCase2<Void, JobScheduleBundle> {
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
    protected Observable<Void> buildUseCaseObservable(final JobScheduleBundle form) {
        return Observable.defer(new Func0<Observable<Void>>() {
            @Override
            public Observable<Void> call() {
                try {
                    mScheduleRepository.update(form);
                    return Observable.just(null);
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        }).cache();
    }
}
