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

package com.jaspersoft.android.jaspermobile.ui.presenter;

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobResource;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.component.presenter.SimplePresenter;
import com.jaspersoft.android.jaspermobile.ui.contract.JobInfoContract;
import com.jaspersoft.android.jaspermobile.ui.navigation.Page;
import com.jaspersoft.android.jaspermobile.ui.navigation.PageFactory;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerActivity
public class JobInfoPresenter extends SimplePresenter<JobInfoContract.View, JobInfoContract.Model, JobInfoContract.ResultCallback>
        implements JobInfoContract.EventListener, JobInfoContract.ResultCallback {

    public static final int EDIT_JOB_REQUEST = 5512;

    private final PageFactory mPageFactory;
    private final RequestExceptionHandler mRequestExceptionHandler;

    @Inject
    public JobInfoPresenter(PageFactory pageFactory, RequestExceptionHandler requestExceptionHandler) {
        mPageFactory = pageFactory;
        mRequestExceptionHandler = requestExceptionHandler;
    }

    @Override
    public void onInit() {
        JobResource jobResource = getModel().getJobDetails();
        getView().showInfo(jobResource);
        getView().showEnableAction(jobResource.getState() == JobResource.NORMAL || jobResource.getState() == JobResource.EXECUTING);
    }

    @Override
    public void onEdit() {
        Page editJobPage = mPageFactory.createJobEditPage(getModel().getJobDetails().getId());
        getNavigator().navigateForResult(editJobPage, EDIT_JOB_REQUEST);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDelete() {
        getView().showLoading();
        getModel().requestJobDeletion();
    }

    @Override
    public void onDeletionSuccess() {
        getView().hideLoading();
        getView().showDeleted();
        getNavigator().navigateUp();
    }

    @Override
    public void onError(Throwable ex) {
        getView().hideLoading();
        mRequestExceptionHandler.showAuthErrorIfExists(ex);
    }
}
