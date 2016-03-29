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
import com.jaspersoft.android.jaspermobile.data.entity.mapper.ResourceMapper;
import com.jaspersoft.android.jaspermobile.data.entity.mapper.ResourcesSortMapper;
import com.jaspersoft.android.jaspermobile.domain.entity.Sort;
import com.jaspersoft.android.jaspermobile.domain.store.SearchQueryStore;
import com.jaspersoft.android.jaspermobile.domain.store.SortStore;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerActivity
public class LibraryCatalogLoaderFactory extends CatalogLoadersFactory {

    private final Context mContext;
    private final JasperRestClient mClient;
    private final SortStore mSortStore;
    private final SearchQueryStore mSearchQueryStore;
    private final ResourceMapper mResourceMapper;
    private final ResourcesSortMapper mResourcesSortMapper;

    @Inject
    public LibraryCatalogLoaderFactory(@ApplicationContext Context context, JasperRestClient client, SortStore sortStore,
                                       SearchQueryStore searchQueryStore, ResourceMapper resourceMapper, ResourcesSortMapper resourcesSortMapper) {
        mContext = context;
        mClient = client;
        mSortStore = sortStore;
        mSearchQueryStore = searchQueryStore;
        mResourceMapper = resourceMapper;
        mResourcesSortMapper = resourcesSortMapper;
    }

    @Override
    public CatalogLoader createLoader() {
        Sort sort = mSortStore.getSortType();
        String searchQuery = mSearchQueryStore.getQuery();
        return new SearchResourcesLoader(mContext, mClient, mResourcesSortMapper.to(sort), searchQuery, mResourceMapper);
    }
}
