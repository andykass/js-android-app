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

package com.jaspersoft.android.jaspermobile.ui.presenter.resources;

import com.jaspersoft.android.jaspermobile.domain.entity.ResourceIcon;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobResource;
import com.jaspersoft.android.jaspermobile.domain.model.JobResourceModel;
import com.jaspersoft.android.jaspermobile.ui.contract.JobResourceContract;
import com.jaspersoft.android.jaspermobile.ui.eventbus.JobResourcesBus;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class JobsResourcePresenter extends ResourcePresenter<JobResourceContract.View, JobResourceModel, JobResource> implements JobResourceContract.EventListener {

    private final JobResourcesBus mJobResourcesBus;

    public JobsResourcePresenter(JobResourceModel resourceModel, JobResource jobResource, JobResourcesBus mJobResourcesBus) {
        super(resourceModel, jobResource);
        this.mJobResourcesBus = mJobResourcesBus;
    }

    @Override
    protected void onBindView(JobResourceContract.View view) {
        super.onBindView(view);

        getView().showImage();
        getView().showSubTitle(getEntity().getFireDate());

        int state = getEntity().getState();
        getView().showEnabled(state == JobResource.NORMAL || state == JobResource.EXECUTING);
        getView().showProgress(!getModel().isInAction(getEntity().getId()));

        ResourceIcon resourceIcon = getModel().getResourceIcon(getEntity().getId());
        if (resourceIcon == null) {
            getModel().requestThumbnail(getEntity().getId(), getEntity().getJobTarget().getReportUri());
        } else {
            getView().showThumbnail(resourceIcon);
        }
    }

    @Override
    public void onSelect() {
        mJobResourcesBus.sendSelectEvent(getEntity());
    }

    @Override
    public void onEnableAction() {

    }

    @Override
    public void onEditAction() {
        mJobResourcesBus.sendEditRequestEvent(getEntity().getId());
    }

    @Override
    public void onDeleteAction() {
        mJobResourcesBus.sendDeleteRequestEvent(getEntity().getId());
    }
}
