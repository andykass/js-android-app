package com.jaspersoft.android.jaspermobile.ui.model;

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleBundle;
import com.jaspersoft.android.jaspermobile.domain.interactor.schedule.GetJobScheduleUseCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.schedule.UpdateJobScheduleUseCase;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewBundle;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiEntityMapper;

import rx.Subscriber;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@PerScreen
public final class ScheduleUpdateModel extends AbstractScheduleModel {
    @NonNull
    private final UiEntityMapper<JobScheduleBundle, JobFormViewBundle> bundleMapper;
    @NonNull
    private final GetJobScheduleUseCase getJobScheduleUseCase;
    @NonNull
    private final UpdateJobScheduleUseCase updateJobScheduleUseCase;
    @NonNull
    private final Analytics analytics;
    private final int jobId;
    private boolean loadDataEventConsumed;

    public ScheduleUpdateModel(
            int jobId,
            @NonNull UiEntityMapper<JobScheduleBundle, JobFormViewBundle> bundleMapper,
            @NonNull GetJobScheduleUseCase getJobScheduleUseCase,
            @NonNull UpdateJobScheduleUseCase updateJobScheduleUseCase,
            @NonNull Analytics analytics
    ) {
        this.jobId = jobId;
        this.bundleMapper = bundleMapper;
        this.getJobScheduleUseCase = getJobScheduleUseCase;
        this.updateJobScheduleUseCase = updateJobScheduleUseCase;
        this.analytics = analytics;
    }

    @Override
    public void load() {
        getJobScheduleUseCase.execute(jobId, createReadFormSubscriber());
    }

    @Override
    public void submit(JobFormViewBundle viewEntity) {
        JobScheduleBundle form = bundleMapper.toDomainEntity(viewEntity);
        updateJobScheduleUseCase.execute(form, createUpdateFormSubscriber());
    }

    @Override
    public void bind(Callback callbacks) {
        super.bind(callbacks);
        if (!loadDataEventConsumed) {
            getJobScheduleUseCase.subscribe(createReadFormSubscriber());
        }
        updateJobScheduleUseCase.subscribe(createUpdateFormSubscriber());
    }

    private Subscriber<? super JobScheduleBundle> createReadFormSubscriber() {
        return new Subscriber<JobScheduleBundle>() {
            @Override
            public void onCompleted() {
                logJobCreatedEvent();
            }

            @Override
            public void onError(Throwable e) {
                mCallbacks.onFormLoadError(e);
            }

            @Override
            public void onNext(JobScheduleBundle item) {
                loadDataEventConsumed = true;
                JobFormViewBundle form = bundleMapper.toUiEntity(item);
                mCallbacks.onFormLoadSuccess(form);
            }
        };
    }

    private Subscriber<? super Void> createUpdateFormSubscriber() {
        return new Subscriber<Void>() {
            @Override
            public void onCompleted() {
                logJobViewedEvent();
            }

            @Override
            public void onError(Throwable e) {
                mCallbacks.onFormSubmitError(e);
            }

            @Override
            public void onNext(Void item) {
                mCallbacks.onFormSubmitSuccess();
            }
        };
    }

    @Override
    public void unbind() {
        super.unbind();
        getJobScheduleUseCase.unsubscribe();
        updateJobScheduleUseCase.unsubscribe();
    }

    private void logJobViewedEvent() {
        analytics.sendEvent(
                Analytics.EventCategory.JOB.getValue(),
                Analytics.EventAction.VIEWED.getValue(),
                null);
    }

    private void logJobCreatedEvent() {
        analytics.sendEvent(
                Analytics.EventCategory.JOB.getValue(),
                Analytics.EventAction.CHANGED.getValue(),
                null);
    }
}
