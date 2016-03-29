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

package com.jaspersoft.android.jaspermobile.data.entity.mapper;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.internal.di.PerProfile;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.jaspermobile.util.resource.JobResource;
import com.jaspersoft.android.sdk.service.data.repository.Resource;
import com.jaspersoft.android.sdk.service.data.schedule.JobState;
import com.jaspersoft.android.sdk.service.data.schedule.JobUnit;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerProfile
public class JobsMapper {

    private final Context mContext;

    @Inject
    public JobsMapper(@ApplicationContext Context context) {
        mContext = context;
    }

    @NonNull
    public List<JasperResource> toJasperResources(@NonNull List<JobUnit> jobs) {
        List<JasperResource> list = new ArrayList<>(jobs.size());
        for (JobUnit job : jobs) {
            if (job != null) {
                JasperResource jasperResource = new JobResource(String.valueOf(job.getId()), job.getLabel(),
                        job.getDescription(), job.getNextFireTime(), parseJobState(job.getState()));
                list.add(jasperResource);
            }
        }
        return list;
    }

    private String parseJobState(JobState jobState) {
        switch (jobState) {
            case NORMAL:
                return mContext.getString(R.string.sch_state_normal);
            case COMPLETE:
                return mContext.getString(R.string.sch_state_complete);
            case EXECUTING:
                return mContext.getString(R.string.sch_state_executing);
            case ERROR:
                return mContext.getString(R.string.sch_state_error);
            case PAUSED:
                return mContext.getString(R.string.sch_state_paused);
            default:
                return mContext.getString(R.string.sch_state_unknown);
        }
    }
}
