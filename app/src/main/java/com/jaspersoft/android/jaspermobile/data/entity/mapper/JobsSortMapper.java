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

import com.jaspersoft.android.jaspermobile.domain.entity.job.JobSort;
import com.jaspersoft.android.jaspermobile.domain.entity.Sort;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;
import com.jaspersoft.android.sdk.service.report.schedule.JobSortType;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerScreen
public class JobsSortMapper {

    @Inject
    public JobsSortMapper() {
    }

    public Sort from(JobSortType jobSortType) {
        switch (jobSortType) {
            case SORTBY_JOBID:
                return new JobSort(JobSort.SORT_BY_ID);
            case SORTBY_JOBNAME:
                return new JobSort(JobSort.SORT_BY_NAME);
            case SORTBY_REPORTURI:
                return new JobSort(JobSort.SORT_BY_REPORT_URI);
            case SORTBY_REPORTNAME:
                return new JobSort(JobSort.SORT_BY_REPORT_NAME);
            case SORTBY_REPORTFOLDER:
                return new JobSort(JobSort.SORT_BY_REPORT_FOLDER);
            case SORTBY_OWNER:
                return new JobSort(JobSort.SORT_BY_OWNER);
            case SORTBY_STATUS:
                return new JobSort(JobSort.SORT_BY_STATUS);
            case SORTBY_LASTRUN:
                return new JobSort(JobSort.SORT_BY_LAST_RUN);
            case SORTBY_NEXTRUN:
                return new JobSort(JobSort.SORT_BY_NEXT_RUN);
            default:
                return new JobSort(JobSort.SORT_BY_NAME);
        }
    }

    public JobSortType to(Sort sort) {
        String sortType = sort.getSortType();
        if (sortType.equals(JobSort.SORT_BY_ID)) return JobSortType.SORTBY_JOBID;
        if (sortType.equals(JobSort.SORT_BY_NAME)) return JobSortType.SORTBY_JOBNAME;
        if (sortType.equals(JobSort.SORT_BY_REPORT_URI)) return JobSortType.SORTBY_REPORTURI;
        if (sortType.equals(JobSort.SORT_BY_REPORT_NAME)) return JobSortType.SORTBY_REPORTNAME;
        if (sortType.equals(JobSort.SORT_BY_REPORT_FOLDER)) return JobSortType.SORTBY_REPORTFOLDER;
        if (sortType.equals(JobSort.SORT_BY_OWNER)) return JobSortType.SORTBY_OWNER;
        if (sortType.equals(JobSort.SORT_BY_STATUS)) return JobSortType.SORTBY_STATUS;
        if (sortType.equals(JobSort.SORT_BY_LAST_RUN)) return JobSortType.SORTBY_LASTRUN;
        if (sortType.equals(JobSort.SORT_BY_NEXT_RUN)) return JobSortType.SORTBY_NEXTRUN;
        return JobSortType.SORTBY_JOBNAME;
    }
}
