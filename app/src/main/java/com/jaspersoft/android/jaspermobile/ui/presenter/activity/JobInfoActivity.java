/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from TIBCO Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of TIBCO Jaspersoft Mobile for Android.
 *
 * TIBCO Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TIBCO Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TIBCO Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.ui.presenter.activity;

import android.app.Activity;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobResource;
import com.jaspersoft.android.jaspermobile.internal.di.components.screen.JobInfoScreenComponent;
import com.jaspersoft.android.jaspermobile.internal.di.components.screen.activity.JobInfoActivityComponent;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.job.JobInfoActivityModule;
import com.jaspersoft.android.jaspermobile.internal.di.modules.screen.job.JobInfoScreenModule;
import com.jaspersoft.android.jaspermobile.ui.component.activity.PresenterControllerActivity3;
import com.jaspersoft.android.jaspermobile.ui.presenter.JobInfoPresenter;
import com.jaspersoft.android.jaspermobile.ui.view.widget.JobInfoView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@EActivity(R.layout.activity_job_info)
public class JobInfoActivity extends PresenterControllerActivity3<JobInfoScreenComponent> {

    @Extra
    protected JobResource jobResource;

    @ViewById(R.id.jobInfo)
    JobInfoView mJobInfoView;

    @Inject
    JobInfoPresenter mJobInfoPresenter;

    @AfterViews
    void init() {
        JobInfoActivityComponent activityComponent = activityComponent();
        activityComponent.inject(this);
        mJobInfoView.setEventListener(mJobInfoPresenter);
        registerPresenter(mJobInfoPresenter);
    }

    @OnActivityResult(JobInfoPresenter.EDIT_JOB_REQUEST)
    void onJobEdited(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
           finish();
        }
    }

    @Override
    protected JobInfoScreenComponent onCreateNonConfigurationComponent() {
        return getProfileComponent().plus(new JobInfoScreenModule(jobResource));
    }

    private JobInfoActivityComponent activityComponent() {
        return getComponent().plus(new JobInfoActivityModule(this));
    }
}
