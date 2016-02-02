package com.jaspersoft.android.jaspermobile.internal.di.components;

import com.jaspersoft.android.jaspermobile.activities.info.fragments.ReportInfoFragment;
import com.jaspersoft.android.jaspermobile.activities.info.fragments.ResourceInfoFragment;
import com.jaspersoft.android.jaspermobile.activities.info.fragments.SimpleInfoFragment;
import com.jaspersoft.android.jaspermobile.activities.library.fragment.LibraryFragment;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.RepositoryFragment;
import com.jaspersoft.android.jaspermobile.activities.schedule.JobsFragment;
import com.jaspersoft.android.jaspermobile.activities.schedule.ScheduleActivity;
import com.jaspersoft.android.jaspermobile.dialog.ReportOptionsFragmentDialog;
import com.jaspersoft.android.jaspermobile.internal.di.PerProfile;
import com.jaspersoft.android.jaspermobile.internal.di.modules.ProfileModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.DashboardModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportRestViewerModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportVisualizeViewerModule;

import dagger.Subcomponent;

/**
 * @author Tom Koptel
 * @since 2.3
 */
@PerProfile
@Subcomponent(
        modules = ProfileModule.class
)
public interface ProfileComponent {
    ReportRestViewerComponent plusReportRestViewer(ActivityModule activityModule,
                                                   ReportRestViewerModule reportModule);

    ReportVisualizeViewerComponent plusReportVisualizeViewer(ActivityModule activityModule,
                                                             ReportVisualizeViewerModule webViewModule);

    ControlsActivityComponent plusControlsPage(ActivityModule activityModule,
                                               ReportModule reportModule);

    DashboardActivityComponent plusDashboardPage(ActivityModule activityModule,
                                                DashboardModule dashboardModule);

    /**
     * TODO remove one after architecture will be revised
     * Hardcoded injections.
     */

    void inject(LibraryFragment libraryFragment);
    void inject(RepositoryFragment repositoryFragment);
    void inject(ReportOptionsFragmentDialog reportOptionsFragmentDialog);
    void inject(SimpleInfoFragment simpleInfoFragment);
    void inject(ResourceInfoFragment resourceInfoFragment);
    void inject(ReportInfoFragment reportInfoFragment);
    void inject(ScheduleActivity scheduleActivity);
    void inject(JobsFragment jobsFragment);
}
