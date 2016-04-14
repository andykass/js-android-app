package com.jaspersoft.android.jaspermobile.domain.interactor.schedule;

import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.executor.PreExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.interactor.AbstractUseCase2;
import com.jaspersoft.android.jaspermobile.domain.repository.schedule.ScheduleRepository;
import com.jaspersoft.android.jaspermobile.domain.repository.schedule.ScheduleSpecification;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func0;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@PerScreen
public class DeleteJobScheduleUseCase extends AbstractUseCase2<Void, Integer> {
    private ScheduleRepository mScheduleRepository;

    @Inject
    public DeleteJobScheduleUseCase(
            PreExecutionThread preExecutionThread,
            PostExecutionThread postExecutionThread,
            ScheduleRepository scheduleRepository
    ) {
        super(preExecutionThread, postExecutionThread);
        mScheduleRepository = scheduleRepository;
    }

    @Override
    protected Observable<Void> buildUseCaseObservable(final Integer jobId) {
        return Observable.defer(new Func0<Observable<Void>>() {
            @Override
            public Observable<Void> call() {
                try {
                    ScheduleSpecification scheduleSpecification = new ScheduleSpecification(jobId);
                    mScheduleRepository.remove(scheduleSpecification);
                    return Observable.just(null);
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        }).cache();
    }
}
