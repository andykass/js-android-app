package com.jaspersoft.android.jaspermobile.ui.model;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleBundle;
import com.jaspersoft.android.jaspermobile.domain.interactor.schedule.SaveJobScheduleUseCase;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewBundle;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiEntityMapper;

import rx.Subscriber;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public final class ScheduleCreateModel extends AbstractScheduleModel {
    @NonNull
    private final JobFormViewBundle scheduleBundle;
    @NonNull
    private final UiEntityMapper<JobScheduleBundle, JobFormViewBundle> bundleMapper;
    @NonNull
    private final SaveJobScheduleUseCase saveJobScheduleUseCase;
    @NonNull
    private final Analytics mAnalytics;

    public ScheduleCreateModel(@NonNull JobFormViewBundle scheduleBundle,
                               @NonNull UiEntityMapper<JobScheduleBundle, JobFormViewBundle> bundleMapper,
                               @NonNull SaveJobScheduleUseCase saveJobScheduleUseCase,
                               @NonNull Analytics analytics) {
        this.scheduleBundle = scheduleBundle;
        this.bundleMapper = bundleMapper;
        this.saveJobScheduleUseCase = saveJobScheduleUseCase;
        this.mAnalytics = analytics;
    }

    @Override
    public void load() {
        mCallbacks.onFormLoadSuccess(scheduleBundle);
    }

    @Override
    public void submit(JobFormViewBundle form) {
        JobScheduleBundle jobForm = bundleMapper.toDomainEntity(form);
        saveJobScheduleUseCase.execute(jobForm, createSubmitSubscriber());
    }

    @Override
    public void bind(Callback callbacks) {
        super.bind(callbacks);
        saveJobScheduleUseCase.subscribe(createSubmitSubscriber());
    }

    @Override
    public void unbind() {
        super.unbind();
        saveJobScheduleUseCase.unsubscribe();
    }

    private Subscriber<? super Void> createSubmitSubscriber() {
        return new Subscriber<Void>() {
            @Override
            public void onError(Throwable e) {
                mCallbacks.onFormSubmitError(e);
            }

            @Override
            public void onNext(Void jobData) {
                mCallbacks.onFormSubmitSuccess();
            }

            @Override
            public void onCompleted() {
                logJobCreatedEvent();
            }
        };
    }

    private void logJobCreatedEvent() {
        mAnalytics.sendEvent(
                Analytics.EventCategory.JOB.getValue(),
                Analytics.EventAction.ADDED.getValue(),
                null);
    }
}
