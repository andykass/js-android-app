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

package com.jaspersoft.android.jaspermobile.data.fetchers;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.jaspersoft.android.jaspermobile.data.entity.LoaderResult;
import com.jaspersoft.android.jaspermobile.data.loaders.CatalogLoader;
import com.jaspersoft.android.jaspermobile.data.loaders.CatalogLoadersFactory;
import com.jaspersoft.android.jaspermobile.domain.fetchers.CatalogFetcher;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerActivity
public class CatalogFetcherImpl implements CatalogFetcher, LoaderManager.LoaderCallbacks<LoaderResult<List<JasperResource>>> {

    private final CatalogLoadersFactory mCatalogLoadersFactory;
    private final LoaderManager mLoaderManager;
    private final int mLoaderId;
    private LoaderCallback mLoaderCallback;
    private CatalogLoader mCatalogLoader;

    @Inject
    public CatalogFetcherImpl(
            CatalogLoadersFactory catalogLoadersFactory,
            LoaderManager loaderManager,
            int loaderId) {
        mCatalogLoadersFactory = catalogLoadersFactory;
        mLoaderManager = loaderManager;
        mLoaderId = loaderId;
    }

    @Override
    public void initSearch(LoaderCallback loaderCallback) {
        mLoaderCallback = loaderCallback;
        mCatalogLoader = (CatalogLoader) mLoaderManager.initLoader(mLoaderId, null, this);
    }

    @Override
    public void resetSearch() {
        mCatalogLoader = (CatalogLoader) mLoaderManager.restartLoader(mLoaderId, null, this);
    }

    @Override
    public void requestNext() {
        if (mCatalogLoader.loadAvailable()) {
            mCatalogLoader.forceLoad();
            mLoaderCallback.onLoadStarted();
        }
    }

    @Override
    public JasperResource fetch(String id) {
        return mCatalogLoader.fetchById(id);
    }

    @Override
    public Loader<LoaderResult<List<JasperResource>>> onCreateLoader(int id, Bundle args) {
        return mCatalogLoadersFactory.createLoader();
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<List<JasperResource>>> loader, LoaderResult<List<JasperResource>> result) {
        if (result.hasResult()) {
            List<JasperResource> resourceList = result.getResult();
            mLoaderCallback.onLoaded(resourceList);
        } else {
            mLoaderCallback.onError(result.getServiceException());
        }
        if (((CatalogLoader) loader).isLoading()) {
            mLoaderCallback.onLoadStarted();
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<List<JasperResource>>> loader) {
    }
}
