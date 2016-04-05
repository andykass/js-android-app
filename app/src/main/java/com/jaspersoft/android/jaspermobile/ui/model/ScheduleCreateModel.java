package com.jaspersoft.android.jaspermobile.ui.model;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.domain.interactor.schedule.SaveJobScheduleUseCase;
import com.jaspersoft.android.jaspermobile.ui.view.entity.JobFormMapper;
import com.jaspersoft.android.jaspermobile.ui.view.entity.JobFormViewEntity;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.sdk.service.data.schedule.JobData;
import com.jaspersoft.android.sdk.service.data.schedule.JobForm;

import rx.Subscriber;

/**
 * @author Tom Koptel
 * @since 2.5
 */
public final class ScheduleCreateModel extends AbstractScheduleModel {
    private final JasperResource mResource;
    private final JobFormMapper mMapper;
    private SaveJobScheduleUseCase mSaveJobScheduleUseCase;
    private Analytics mAnalytics;

    public ScheduleCreateModel(
            JasperResource resource,
            JobFormMapper mapper,
            SaveJobScheduleUseCase saveJobScheduleUseCase,
            Analytics analytics
    ) {
        mMapper = mapper;
        mResource = resource;
        mSaveJobScheduleUseCase = saveJobScheduleUseCase;
        mAnalytics = analytics;
    }

    @Override
    public void load() {
        JobFormViewEntity form = mMapper.toUiEntity(mResource);
        mCallbacks.onFormLoadSuccess(form);
    }

    @Override
    public void submit(JobFormViewEntity form) {
        JobForm jobForm = mMapper.toDataEntity(form);
        mSaveJobScheduleUseCase.execute(jobForm, createSubmitSubscriber());
    }

    @Override
    public void bind(Callback callbacks) {
        super.bind(callbacks);
        mSaveJobScheduleUseCase.subscribe(createSubmitSubscriber());
    }

    @Override
    public void unbind() {
        super.unbind();
        mSaveJobScheduleUseCase.unsubscribe();
    }

    private Subscriber<? super JobData> createSubmitSubscriber() {
        return new Subscriber<JobData>() {
            @Override
            public void onError(Throwable e) {
                mCallbacks.onFormSubmitError(e);
            }

            @Override
            public void onNext(JobData jobData) {
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
