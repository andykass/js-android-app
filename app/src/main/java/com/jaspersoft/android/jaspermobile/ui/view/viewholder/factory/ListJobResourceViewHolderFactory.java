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

package com.jaspersoft.android.jaspermobile.ui.view.viewholder.factory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.entity.job.JobResource;
import com.jaspersoft.android.jaspermobile.internal.di.ActivityContext;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.JobResourceViewHolder;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerActivity
public class ListJobResourceViewHolderFactory extends ListResourceViewHolderFactory<JobResource, JobResourceViewHolder> {

    private final static int JOB_HOLDER_ID = 1;

    @Inject
    public ListJobResourceViewHolderFactory(@ActivityContext Context mContext) {
        super(mContext);
    }

    @Override
    protected int getHolderIdInternal(JobResource resource) {
        return JOB_HOLDER_ID;
    }

    @Override
    public JobResourceViewHolder internalCreate(ViewGroup parentView, int viewType) {
        View reportResourceView = LayoutInflater.from(mContext).
                inflate(R.layout.item_job_resource, parentView, false);
        return new JobResourceViewHolder(reportResourceView);
    }

    @Override
    public JobResourceViewHolder createLoadingViewHolder(ViewGroup parentView) {
        View reportResourceView = LayoutInflater.from(mContext).
                inflate(R.layout.item_resource_list_loading, parentView, false);
        return new JobResourceViewHolder(reportResourceView);
    }
}
