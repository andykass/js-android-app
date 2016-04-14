package com.jaspersoft.android.jaspermobile.internal.di.components.screen;

import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.jaspermobile.internal.di.components.screen.activity.JobFormActivityComponent;
import com.jaspersoft.android.jaspermobile.internal.di.modules.screen.job.JobFormScreenModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.job.JobFormActivityModule;

import dagger.Subcomponent;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@PerScreen
@Subcomponent(
        modules = JobFormScreenModule.class
)
public interface JobFormScreenComponent {
    JobFormActivityComponent plus(JobFormActivityModule module);
}
