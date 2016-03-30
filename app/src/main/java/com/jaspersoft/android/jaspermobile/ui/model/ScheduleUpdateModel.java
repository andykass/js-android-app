package com.jaspersoft.android.jaspermobile.ui.model;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.domain.interactor.schedule.GetJobScheduleUseCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.schedule.UpdateJobScheduleUseCase;
import com.jaspersoft.android.jaspermobile.domain.request.UpdateJobRequest;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.jaspermobile.ui.view.entity.JobFormMapper;
import com.jaspersoft.android.jaspermobile.ui.view.entity.JobFormViewEntity;
import com.jaspersoft.android.sdk.service.data.schedule.JobForm;

import rx.Subscriber;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@PerScreen
public final class ScheduleUpdateModel extends AbstractScheduleModel {
    private int mJobId;
    private JobFormMapper mMapper;
    private GetJobScheduleUseCase mGetJobScheduleUseCase;
    private UpdateJobScheduleUseCase mUpdateJobScheduleUseCase;
    private Analytics mAnalytics;

    public ScheduleUpdateModel(
            int jobId,
            JobFormMapper mapper,
            GetJobScheduleUseCase getJobScheduleUseCase,
            UpdateJobScheduleUseCase updateJobScheduleUseCase,
            Analytics analytics
    ) {
        mJobId = jobId;
        mMapper = mapper;
        mGetJobScheduleUseCase = getJobScheduleUseCase;
        mUpdateJobScheduleUseCase = updateJobScheduleUseCase;
        mAnalytics = analytics;
    }

    @Override
    public void load() {
        mGetJobScheduleUseCase.execute(mJobId, createReadFormSubscriber());
    }

    @Override
    public void submit(JobFormViewEntity viewEntity) {
        JobForm form = mMapper.toDataEntity(viewEntity);
        UpdateJobRequest request = new UpdateJobRequest(mJobId, form);
        mUpdateJobScheduleUseCase.execute(request, createUpdateFormSubscriber());
    }

    @Override
    public void bind(Callback callbacks) {
        super.bind(callbacks);
        mGetJobScheduleUseCase.subscribe(createReadFormSubscriber());
        mUpdateJobScheduleUseCase.subscribe(createUpdateFormSubscriber());
    }

    private Subscriber<? super JobForm> createReadFormSubscriber() {
        return new Subscriber<JobForm>() {
            @Override
            public void onCompleted() {
                logJobCreatedEvent();
            }

            @Override
            public void onError(Throwable e) {
                mCallbacks.onFormLoadError(e);
            }

            @Override
            public void onNext(JobForm item) {
                JobFormViewEntity form = mMapper.toUiEntity(item);
                mCallbacks.onFormLoadSuccess(form);
            }
        };
    }

    private Subscriber<? super JobForm> createUpdateFormSubscriber() {
        return new Subscriber<JobForm>() {
            @Override
            public void onCompleted() {
                logJobViewedEvent();
            }

            @Override
            public void onError(Throwable e) {
                mCallbacks.onFormSubmitError(e);
            }

            @Override
            public void onNext(JobForm item) {
                mCallbacks.onFormSubmitSuccess();
            }
        };
    }

    @Override
    public void unbind() {
        super.unbind();
        mGetJobScheduleUseCase.unsubscribe();
        mUpdateJobScheduleUseCase.unsubscribe();
    }

    private void logJobViewedEvent() {
        mAnalytics.sendEvent(
                Analytics.EventCategory.JOB.getValue(),
                Analytics.EventAction.VIEWED.getValue(),
                null);
    }

    private void logJobCreatedEvent() {
        mAnalytics.sendEvent(
                Analytics.EventCategory.JOB.getValue(),
                Analytics.EventAction.CHANGED.getValue(),
                null);
    }
}
