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
import com.jaspersoft.android.jaspermobile.domain.repository.library.LibrarySortRepository;
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
    private final LibrarySortRepository mLibrarySortRepository;
    private final ResourceMapper mResourceMapper;

    @Inject
    public LibraryCatalogLoaderFactory(@ApplicationContext Context context, JasperRestClient client, LibrarySortRepository librarySortRepository, ResourceMapper resourceMapper) {
        mContext = context;
        mClient = client;
        mLibrarySortRepository = librarySortRepository;
        mResourceMapper = resourceMapper;
    }

    @Override
    public CatalogLoader createLoader() {
        return new SearchResourcesLoader(mContext, mClient, mLibrarySortRepository, mResourceMapper);
    }
}
