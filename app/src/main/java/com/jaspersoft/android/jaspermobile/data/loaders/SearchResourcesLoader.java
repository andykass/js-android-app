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
import android.support.v4.content.AsyncTaskLoader;

import com.jaspersoft.android.jaspermobile.data.JasperRestClient;
import com.jaspersoft.android.jaspermobile.data.entity.LoaderResult;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.sdk.service.data.repository.Resource;
import com.jaspersoft.android.sdk.service.exception.ServiceException;
import com.jaspersoft.android.sdk.service.repository.RepositorySearchCriteria;
import com.jaspersoft.android.sdk.service.repository.RepositorySearchTask;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class SearchResourcesLoader extends AsyncTaskLoader<LoaderResult<List<Resource>>> {

    private final RepositorySearchTask mRepositorySearchTask;
    private List<Resource> mResources;
    boolean mIsLoading;

    @Inject
    public SearchResourcesLoader(@ApplicationContext Context context, JasperRestClient client, RepositorySearchCriteria resourceSearchCriteria) {
        super(context);

        mRepositorySearchTask = client.syncRepositoryService().search(resourceSearchCriteria);
        mResources = new ArrayList<>();
    }

    @Override
    protected void onStartLoading() {
        if (mResources.isEmpty()) {
            forceLoad();
        } else {
            deliverResult(new LoaderResult<>(mResources));
        }
    }

    @Override
    public LoaderResult<List<Resource>> loadInBackground() {
        mIsLoading = true;
        try {
            List<Resource> searchResult = mRepositorySearchTask.nextLookup();
            mResources.addAll(searchResult);
            return new LoaderResult<>(searchResult);
        } catch (ServiceException e) {
            return new LoaderResult<>(e);
        }
    }

    @Override
    public void deliverResult(LoaderResult<List<Resource>> loaderResult) {
        mIsLoading = false;

        if (isStarted()) {
            super.deliverResult(loaderResult.hasResult() ? new LoaderResult<>(mResources) : loaderResult);
        }
    }

    public boolean loadAvailable() {
        return mRepositorySearchTask.hasNext() && !isLoading();
    }

    public boolean isLoading() {
        return mIsLoading;
    }
}
