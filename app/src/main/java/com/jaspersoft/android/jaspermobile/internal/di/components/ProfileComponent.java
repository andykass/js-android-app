package com.jaspersoft.android.jaspermobile.internal.di.components;

import com.jaspersoft.android.jaspermobile.activities.save.ReportDownloadManager;
import com.jaspersoft.android.jaspermobile.activities.save.SaveReportService;
import com.jaspersoft.android.jaspermobile.domain.Profile;
import com.jaspersoft.android.jaspermobile.internal.di.PerProfile;
import com.jaspersoft.android.jaspermobile.internal.di.components.screen.ChooseReportScreenComponent;
import com.jaspersoft.android.jaspermobile.internal.di.components.screen.JobsScreenComponent;
import com.jaspersoft.android.jaspermobile.internal.di.components.screen.ScheduleFormScreenComponent;
import com.jaspersoft.android.jaspermobile.internal.di.modules.ProfileModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.DashboardModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.NavigationActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportRestViewerModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.ReportVisualizeViewerModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.screen.ScheduleFormScreenModule;
import com.jaspersoft.android.jaspermobile.util.cast.ResourcePresentationService;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.FileResourceBinder;

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

    NavigationActivityComponent plusNavigationPage(NavigationActivityModule module);

    BaseActivityComponent plusBase(ActivityModule activityModule);

    ChooseReportScreenComponent newChooseReportScreen();

    JobsScreenComponent newJobsScreen();

    ScheduleFormScreenComponent plus(ScheduleFormScreenModule module);

    Profile getProfile();
    /**
     * TODO remove one after architecture will be revised
     * Hardcoded injections.
     */
    void inject(FileResourceBinder fileResourceBinder);
    void inject(SaveReportService saveReportService);
    void inject(ResourcePresentationService resourcePresentationService);
    void inject(ReportDownloadManager reportDownloadManager);
}
