package com.jaspersoft.android.jaspermobile.domain.interactor.schedule;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleBundle;
import com.jaspersoft.android.jaspermobile.domain.executor.PostExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.executor.PreExecutionThread;
import com.jaspersoft.android.jaspermobile.domain.interactor.AbstractUseCase2;
import com.jaspersoft.android.jaspermobile.domain.repository.schedule.ScheduleRepository;
import com.jaspersoft.android.jaspermobile.domain.repository.schedule.ScheduleSpecification;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func0;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@PerScreen
public class GetJobScheduleUseCase extends AbstractUseCase2<JobScheduleBundle, Integer> {
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
    protected Observable<JobScheduleBundle> buildUseCaseObservable(final Integer jobId) {
        return Observable.defer(new Func0<Observable<JobScheduleBundle>>() {
            @Override
            public Observable<JobScheduleBundle> call() {
                try {
                    ScheduleSpecification scheduleSpecification = new ScheduleSpecification(jobId);
                    List<JobScheduleBundle> result = mScheduleRepository.query(scheduleSpecification);
                    return Observable.just(result.get(0));
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }
        }).cache();
    }
}
