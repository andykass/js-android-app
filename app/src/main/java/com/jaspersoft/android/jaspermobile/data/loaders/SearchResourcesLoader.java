/*
 * Copyright � 2015 TIBCO Software, Inc. All rights reserved.
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
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.sdk.service.data.repository.Resource;
import com.jaspersoft.android.sdk.service.exception.ServiceException;
import com.jaspersoft.android.sdk.service.repository.RepositorySearchCriteria;
import com.jaspersoft.android.sdk.service.repository.RepositorySearchTask;
import com.jaspersoft.android.sdk.service.repository.SortType;

import java.util.List;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class SearchResourcesLoader extends CatalogLoader {

    private final RepositorySearchTask mRepositorySearchTask;
    private final ResourceMapper mResourceMapper;

    public SearchResourcesLoader(@ApplicationContext Context context, JasperRestClient client, SortType sortType, String searchQuery, ResourceMapper resourceMapper) {
        super(context);
        mResourceMapper = resourceMapper;

        RepositorySearchCriteria repositorySearchCriteria = RepositorySearchCriteria.builder()
                .withFolderUri("/")
                .withLimit(40)
                .withQuery(searchQuery)
                .withRecursive(true)
                .withResourceMask(RepositorySearchCriteria.REPORT)
                .withSortType(sortType)
                .build();

        mRepositorySearchTask = client.syncRepositoryService().search(repositorySearchCriteria);
    }

    @Override
    protected List<JasperResource> loadData() throws ServiceException {
        List<Resource> resources = mRepositorySearchTask.nextLookup();
        return mResourceMapper.toJasperResources(resources);
    }

    @Override
    public boolean loadAvailable() {
        return mRepositorySearchTask.hasNext() && !isLoading();
    }
}
