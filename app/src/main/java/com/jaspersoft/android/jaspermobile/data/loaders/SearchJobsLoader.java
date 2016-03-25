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

package com.jaspersoft.android.jaspermobile.data.loaders;

import android.content.Context;

import com.jaspersoft.android.jaspermobile.data.JasperRestClient;
import com.jaspersoft.android.jaspermobile.data.entity.mapper.JobsMapper;
import com.jaspersoft.android.jaspermobile.data.repository.resources.JobSearchQueryRepository;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.repository.job.JobSortRepository;
import com.jaspersoft.android.jaspermobile.domain.repository.resources.SearchQueryRepository;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.sdk.service.data.schedule.JobUnit;
import com.jaspersoft.android.sdk.service.exception.ServiceException;
import com.jaspersoft.android.sdk.service.report.schedule.JobSearchCriteria;
import com.jaspersoft.android.sdk.service.report.schedule.JobSearchTask;

import java.util.List;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class SearchJobsLoader extends CatalogLoader {

    private JobSearchTask mJobSearchTask;
    private final JasperRestClient mClient;
    private final JobSortRepository mJobFilterRepository;
    private final SearchQueryRepository mSearchQueryRepository;
    private final JobsMapper mJobsMapper;

    public SearchJobsLoader(@ApplicationContext Context context, JasperRestClient client, JobSortRepository jobFilterRepository, SearchQueryRepository searchQueryRepository, JobsMapper jobsMapper) {
        super(context);
        mClient = client;
        mJobFilterRepository = jobFilterRepository;
        mSearchQueryRepository = searchQueryRepository;
        mJobsMapper = jobsMapper;

        createSearchTask();

        searchQueryRepository.observe().subscribe(new SimpleSubscriber<String>() {
            @Override
            public void onNext(String item) {
                createSearchTask();
                SearchJobsLoader.this.onContentChanged();
            }
        });
    }

    @Override
    protected List<JasperResource> loadData() throws ServiceException {
        List<JobUnit> jobs = mJobSearchTask.nextLookup();
        return mJobsMapper.toJasperResources(jobs);
    }

    @Override
    public boolean loadAvailable() {
        return mJobSearchTask.hasNext() && !isLoading();
    }

    private void createSearchTask() {
        JobSearchCriteria jobSearchCriteria = JobSearchCriteria.builder()
                .withLabel(mSearchQueryRepository.getQuery())
                .withSortType(mJobFilterRepository.getSortType())
                .build();

        mJobSearchTask = mClient.syncScheduleService().search(jobSearchCriteria);
    }
}
