package com.jaspersoft.android.jaspermobile.internal.di.modules.screen.job;

import android.content.Context;

import com.jaspersoft.android.jaspermobile.data.mapper.job.JobDataFormBundleWrapper;
import com.jaspersoft.android.jaspermobile.data.mapper.job.JobDataFormMapper;
import com.jaspersoft.android.jaspermobile.data.repository.job.NetworkScheduleRepository;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobScheduleBundle;
import com.jaspersoft.android.jaspermobile.domain.repository.schedule.ScheduleRepository;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.jaspermobile.ui.entity.job.JobFormViewBundle;
import com.jaspersoft.android.jaspermobile.ui.mapper.UiEntityMapper;
import com.jaspersoft.android.jaspermobile.ui.mapper.job.JasperResourceMapper;
import com.jaspersoft.android.jaspermobile.ui.mapper.job.JobUiFormBundleMapper;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@Module
abstract class JobScreenBaseModule {
    @Provides
    @PerScreen
    public ScheduleRepository providesScheduleRepository(NetworkScheduleRepository scheduleRepository) {
        return scheduleRepository;
    }

    @Provides
    @PerScreen
    public JobDataFormMapper dataDomainMapper() {
        return JobDataFormMapper.create();
    }

    @Provides
    @PerScreen
    public JobDataFormBundleWrapper providesBundleWrapper() {
        return JobDataFormBundleWrapper.create();
    }

    @Provides
    @PerScreen
    public UiEntityMapper<JasperResource, JobFormViewBundle> provideResourceMapper(@ApplicationContext Context context) {
        return JasperResourceMapper.create(context);
    }

    @Provides
    @PerScreen
    public UiEntityMapper<JobScheduleBundle, JobFormViewBundle> provideUiFormBundleMapper(@ApplicationContext Context context) {
        return JobUiFormBundleMapper.create(context);
    }
}
