package com.jaspersoft.android.jaspermobile.internal.di.components.screen.activity;

import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.job.JobFormActivityModule;
import com.jaspersoft.android.jaspermobile.ui.component.presenter.HasPresenter;
import com.jaspersoft.android.jaspermobile.ui.presenter.ScheduleFormPresenter;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.EditScheduleFormFragment;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.NewScheduleFormFragment;
import com.jaspersoft.android.jaspermobile.ui.view.widget.ScheduleFormView;

import dagger.Subcomponent;

/**
 * @author Tom Koptel
 * @since 2.5
 */
@PerActivity
@Subcomponent(
        modules = {
                JobFormActivityModule.class
        }
)
public interface JobFormActivityComponent extends HasPresenter<ScheduleFormPresenter> {
    ScheduleFormView inject(ScheduleFormView scheduleFragment);

    void inject(NewScheduleFormFragment formFragment);
    void inject(EditScheduleFormFragment formFragment);
}
