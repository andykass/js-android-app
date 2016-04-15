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

package com.jaspersoft.android.jaspermobile.internal.di.modules.screen.job;

import com.jaspersoft.android.jaspermobile.data.fetchers.JobsFetcher;
import com.jaspersoft.android.jaspermobile.data.fetchers.ThumbnailFetcherImpl;
import com.jaspersoft.android.jaspermobile.data.model.JobResourceModelImpl;
import com.jaspersoft.android.jaspermobile.data.store.InMemoryJobsSortStore;
import com.jaspersoft.android.jaspermobile.data.store.InMemoryRepresentationStore;
import com.jaspersoft.android.jaspermobile.data.store.JobSearchQueryStore;
import com.jaspersoft.android.jaspermobile.domain.fetchers.CatalogFetcher;
import com.jaspersoft.android.jaspermobile.domain.fetchers.ThumbnailFetcher;
import com.jaspersoft.android.jaspermobile.domain.model.JobResourceModel;
import com.jaspersoft.android.jaspermobile.domain.model.ResourceModel;
import com.jaspersoft.android.jaspermobile.domain.store.RepresentationStore;
import com.jaspersoft.android.jaspermobile.domain.store.SearchQueryStore;
import com.jaspersoft.android.jaspermobile.domain.store.SortStore;
import com.jaspersoft.android.jaspermobile.internal.di.PerScreen;

import dagger.Module;
import dagger.Provides;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@Module
public class JobsScreenModule  {

    @Provides
    @PerScreen
    CatalogFetcher providesCatalogLoader(JobsFetcher fetcher) {
        return fetcher;
    }

    @Provides
    @PerScreen
    ThumbnailFetcher provideThumbnailFetcher(ThumbnailFetcherImpl fetcher) {
        return fetcher;
    }

    @Provides
    @PerScreen
    SortStore provideSortStore(InMemoryJobsSortStore store) {
        return store;
    }

    @Provides
    @PerScreen
    SearchQueryStore provideSearchQueryStore(JobSearchQueryStore store) {
        return store;
    }

    @Provides
    @PerScreen
    RepresentationStore providesRepresentationStore(InMemoryRepresentationStore store) {
        return store;
    }

    @Provides
    @PerScreen
    JobResourceModel provideJobResourceModel(JobResourceModelImpl model) {
        return model;
    }

    @Provides
    @PerScreen
    ResourceModel provideResourceModel(JobResourceModelImpl model) {
        return model;
    }
}
