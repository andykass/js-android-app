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

package com.jaspersoft.android.jaspermobile.internal.di.modules;

import android.support.v4.app.LoaderManager;

import com.jaspersoft.android.jaspermobile.data.fetchers.CatalogFetcherImpl;
import com.jaspersoft.android.jaspermobile.data.loaders.JobsCatalogLoaderFactory;
import com.jaspersoft.android.jaspermobile.data.repository.job.InMemoryJobSortRepository;
import com.jaspersoft.android.jaspermobile.data.repository.resources.JobSearchQueryStore;
import com.jaspersoft.android.jaspermobile.domain.fetchers.CatalogFetcher;
import com.jaspersoft.android.jaspermobile.domain.repository.job.JobSortRepository;
import com.jaspersoft.android.jaspermobile.domain.repository.resources.SearchQueryStore;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@Module
public class JobsModule {

    private static final int JOB_LOADER_ID = 1;

    @Provides
    @PerActivity
    CatalogFetcher providesCatalogLoader(LoaderManager loaderManager, JobsCatalogLoaderFactory loaderFactory) {
        return new CatalogFetcherImpl(loaderFactory, loaderManager, JOB_LOADER_ID);
    }

    @Provides
    @PerActivity
    JobSortRepository provideJobSortRepository(InMemoryJobSortRepository repository) {
        return repository;
    }

    @Provides
    @PerActivity
    SearchQueryStore provideSearchQueryRepository(JobSearchQueryStore repository) {
        return repository;
    }
}
