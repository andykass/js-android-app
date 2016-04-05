package com.jaspersoft.android.jaspermobile.internal.di.components.screen;

import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.jaspermobile.internal.di.components.screen.activity.ScheduleFormActivityComponent;
import com.jaspersoft.android.jaspermobile.internal.di.modules.screen.ScheduleFormScreenModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.screen.activity.ScheduleFormActivityModule;

import dagger.Subcomponent;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@PerScreen
@Subcomponent(
        modules = ScheduleFormScreenModule.class
)
public interface ScheduleFormScreenComponent {
    ScheduleFormActivityComponent plus(ScheduleFormActivityModule module);
}
