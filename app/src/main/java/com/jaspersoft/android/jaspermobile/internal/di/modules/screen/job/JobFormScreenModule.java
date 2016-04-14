package com.jaspersoft.android.jaspermobile.internal.di.modules.screen.job;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleBundle;
import com.jaspersoft.android.jaspermobile.domain.interactor.schedule.GetJobScheduleUseCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.schedule.SaveJobScheduleUseCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.schedule.UpdateJobScheduleUseCase;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.jaspermobile.ui.contract.ScheduleFormContract;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewBundle;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiEntityMapper;
import com.jaspersoft.android.jaspermobile.ui.model.ScheduleCreateModel;
import com.jaspersoft.android.jaspermobile.ui.model.ScheduleUpdateModel;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@Module
public class JobFormScreenModule extends JobScreenBaseModule {
    private static final int NO_ID = Integer.MIN_VALUE;

    private final JasperResource mResource;
    private final int mJobId;

    public JobFormScreenModule(JasperResource resource) {
        mResource = resource;
        mJobId = NO_ID;
    }

    public JobFormScreenModule(int id) {
        mResource = null;
        mJobId = id;
    }

    @Provides
    @PerScreen
    ScheduleFormContract.Model providesModel(
            Analytics analytics,
            GetJobScheduleUseCase getJobScheduleUseCase,
            UpdateJobScheduleUseCase updateJobScheduleUseCase,
            SaveJobScheduleUseCase saveJobScheduleUseCase,
            UiEntityMapper<JobScheduleBundle, JobFormViewBundle> formMapper,
            UiEntityMapper<JasperResource, JobFormViewBundle> resourceMapper
    ) {
        if (mResource == null) {
            return new ScheduleUpdateModel(mJobId, formMapper, getJobScheduleUseCase, updateJobScheduleUseCase, analytics);
        }
        JobFormViewBundle viewBundle = resourceMapper.toUiEntity(mResource);
        return new ScheduleCreateModel(viewBundle, formMapper, saveJobScheduleUseCase, analytics);
    }
}
