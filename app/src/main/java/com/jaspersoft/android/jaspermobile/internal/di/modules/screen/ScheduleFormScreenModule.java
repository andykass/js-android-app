package com.jaspersoft.android.jaspermobile.internal.di.modules.screen;

import com.jaspersoft.android.jaspermobile.Analytics;
import com.jaspersoft.android.jaspermobile.data.repository.schedule.NetworkScheduleRepository;
import com.jaspersoft.android.jaspermobile.domain.interactor.schedule.GetJobScheduleUseCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.schedule.SaveJobScheduleUseCase;
import com.jaspersoft.android.jaspermobile.domain.interactor.schedule.UpdateJobScheduleUseCase;
import com.jaspersoft.android.jaspermobile.domain.repository.schedule.ScheduleRepository;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.jaspermobile.ui.contract.ScheduleFormContract;
import com.jaspersoft.android.jaspermobile.ui.model.ScheduleCreateModel;
import com.jaspersoft.android.jaspermobile.ui.model.ScheduleUpdateModel;
import com.jaspersoft.android.jaspermobile.ui.view.entity.JobFormMapper;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@Module
public class ScheduleFormScreenModule {
    private static final int NO_ID = Integer.MIN_VALUE;

    private final JasperResource mResource;
    private final int mJobId;

    public ScheduleFormScreenModule(JasperResource resource) {
        mResource = resource;
        mJobId = NO_ID;
    }

    public ScheduleFormScreenModule(int id) {
        mResource = null;
        mJobId = id;
    }

    @Provides
    @PerScreen
    ScheduleFormContract.Model providesModel(
            Analytics analytics,
            JobFormMapper mapper,
            GetJobScheduleUseCase getJobScheduleUseCase,
            UpdateJobScheduleUseCase updateJobScheduleUseCase,
            SaveJobScheduleUseCase saveJobScheduleUseCase
    ) {
        if (mResource == null) {
            return new ScheduleUpdateModel(mJobId, mapper, getJobScheduleUseCase, updateJobScheduleUseCase, analytics);
        }
        return new ScheduleCreateModel(mResource, mapper, saveJobScheduleUseCase, analytics);
    }

    @Provides
    @PerScreen
    ScheduleRepository providesScheduleRepository(NetworkScheduleRepository scheduleRepository) {
        return scheduleRepository;
    }

}
