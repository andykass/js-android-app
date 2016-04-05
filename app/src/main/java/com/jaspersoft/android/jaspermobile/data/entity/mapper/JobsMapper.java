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

import android.support.annotation.NonNull;

import com.jaspersoft.android.jaspermobile.domain.entity.JobResource;
import com.jaspersoft.android.jaspermobile.internal.di.PerProfile;
import com.jaspersoft.android.sdk.service.data.schedule.JobState;
import com.jaspersoft.android.sdk.service.data.schedule.JobUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerProfile
public class JobsMapper {

    @Inject
    public JobsMapper() {
    }

    @NonNull
    public List<JobResource> toJobResources(@NonNull Collection<JobUnit> jobs) {
        List<JobResource> jobsResources = new ArrayList<>();
        for (JobUnit job : jobs) {
            JobResource jobResource = toJobResource(job);
            jobsResources.add(jobResource);
        }
        return jobsResources;
    }

    @NonNull
    public JobResource toJobResource(@NonNull JobUnit job) {
        int jobState = parseJobState(job.getState());
        return new JobResource(job.getLabel(), job.getId(), job.getNextFireTime(), jobState);
    }

    private int parseJobState(JobState jobState) {
        switch (jobState) {
            case NORMAL:
                return JobResource.NORMAL;
            case COMPLETE:
                return JobResource.COMPLETE;
            case EXECUTING:
                return JobResource.EXECUTING;
            case ERROR:
                return JobResource.ERROR;
            case PAUSED:
                return JobResource.PAUSED;
            default:
                return JobResource.UNKNOWN;
        }
    }
}
