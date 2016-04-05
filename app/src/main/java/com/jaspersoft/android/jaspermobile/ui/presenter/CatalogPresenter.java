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

package com.jaspersoft.android.jaspermobile.ui.presenter;

import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.entity.Resource;
import com.jaspersoft.android.jaspermobile.domain.fetchers.CatalogFetcher;
import com.jaspersoft.android.jaspermobile.domain.model.JobResourceModel;
import com.jaspersoft.android.jaspermobile.domain.model.ResourceModel;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.component.presenter.BasePresenter;
import com.jaspersoft.android.jaspermobile.ui.contract.CatalogContract;
import com.jaspersoft.android.sdk.service.exception.ServiceException;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerActivity
public class CatalogPresenter extends BasePresenter<CatalogContract.View> implements CatalogContract.EventListener, CatalogFetcher.LoaderCallback {

    private final CatalogFetcher mResourceLoader;
    private final RequestExceptionHandler mRequestExceptionHandler;
    protected final ResourceModel mResourceModel;

    @Inject
    public CatalogPresenter(CatalogFetcher resourceLoader, RequestExceptionHandler requestExceptionHandler, ResourceModel resourceModel) {
        this.mResourceLoader = resourceLoader;
        this.mRequestExceptionHandler = requestExceptionHandler;
        this.mResourceModel = resourceModel;
    }

    public void refresh() {
        mResourceLoader.reset();
    }

    @Override
    public void onBindView(CatalogContract.View view) {
        mResourceModel.subscribe(new SimpleSubscriber<Integer>() {
            @Override
            public void onNext(Integer item) {
                getView().updateResource(item);
            }

            @Override
            public void onError(Throwable e) {
                mRequestExceptionHandler.showAuthErrorIfExists(e);
            }
        });
        mResourceLoader.subscribe(this);
       mResourceLoader.search();
    }

    @Override
    public void onRefresh() {
        mResourceLoader.reset();
    }

    @Override
    public void onScrollToEnd() {
        mResourceLoader.search();
    }

    @Override
    public void onLoadStarted(boolean first) {
        if (first) {
            getView().showFirstLoading();
        } else {
            getView().showNextLoading();
        }
    }

    @Override
    public void onLoaded(List<Resource> resources) {
        getView().hideLoading();
        getView().showResources(resources);
        if (resources.isEmpty()) {
            getView().showEmpty();
        }
    }

    @Override
    public void onError(ServiceException ex, boolean first) {
        mRequestExceptionHandler.showAuthErrorIfExists(ex);
        getView().hideLoading();
        if (first) {
            getView().showError();
        }
    }
}
