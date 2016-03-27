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
import android.support.v4.content.AsyncTaskLoader;

import com.jaspersoft.android.jaspermobile.data.JasperRestClient;
import com.jaspersoft.android.jaspermobile.data.entity.LoaderResult;
import com.jaspersoft.android.jaspermobile.internal.di.ApplicationContext;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.sdk.service.data.schedule.JobUnit;
import com.jaspersoft.android.sdk.service.exception.ServiceException;
import com.jaspersoft.android.sdk.service.report.schedule.JobSearchCriteria;
import com.jaspersoft.android.sdk.service.report.schedule.JobSearchTask;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public abstract class CatalogLoader extends AsyncTaskLoader<LoaderResult<List<JasperResource>>> {
    private List<JasperResource> mResultList;
    private boolean mIsLoading;

    public CatalogLoader(@ApplicationContext Context context) {
        super(context);
        mResultList = new ArrayList<>();
    }

    @Override
    protected final void onStartLoading() {
        if (mResultList.isEmpty()) {
            forceLoad();
        } else {
            deliverResult(new LoaderResult<>(mResultList));
        }
    }

    @Override
    public final LoaderResult<List<JasperResource>> loadInBackground() {
        mIsLoading = true;
        try {
            List<JasperResource> searchResult = loadData();
            mResultList.addAll(searchResult);
            return new LoaderResult<>(searchResult);
        } catch (ServiceException e) {
            return new LoaderResult<>(e);
        }
    }

    @Override
    public final void deliverResult(LoaderResult<List<JasperResource>> loaderResult) {
        mIsLoading = false;

        if (isStarted()) {
            List<JasperResource> listCopy = new ArrayList<>(mResultList);
            super.deliverResult(loaderResult.hasResult() ? new LoaderResult<>(listCopy) : loaderResult);
        }
    }

    @Override
    public void onContentChanged() {
        mResultList = new ArrayList<>();
        super.onContentChanged();
    }

    public final boolean isLoading() {
        return mIsLoading;
    }

    public final JasperResource fetchById(String id) {
        for (JasperResource jasperResource : mResultList) {
            if (jasperResource.getId().equals(id)) {
                return jasperResource;
            }
        }
        return null;
    }

    protected abstract List<JasperResource> loadData() throws ServiceException;

    public abstract boolean loadAvailable();
}
