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

import com.jaspersoft.android.jaspermobile.domain.fetchers.CatalogFetcher;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.network.RequestExceptionHandler;
import com.jaspersoft.android.jaspermobile.ui.contract.CatalogContract;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.sdk.service.exception.ServiceException;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerActivity
public class CatalogPresenter extends BasePresenter<CatalogContract.View> implements CatalogContract.EventListener, CatalogFetcher.LoaderCallback {

    @Inject
    CatalogFetcher mResourceLoader;
    @Inject
    RequestExceptionHandler mRequestExceptionHandler;

    private ItemSelectListener mListener;

    @Inject
    public CatalogPresenter() {
    }

    public void setListener(ItemSelectListener listener) {
        mListener = listener;
    }

    public void refresh() {
        onRefresh();
    }

    @Override
    protected void onBind() {
        getView().showFirstLoading();
        mResourceLoader.initSearch(this);
    }

    @Override
    public void onRefresh() {
        reloadResources();
    }

    @Override
    public void onScrollToEnd() {
        mResourceLoader.requestNext();
    }

    @Override
    public void onItemClick(String itemId) {
        if (mListener == null) return;

        JasperResource jasperResource = mResourceLoader.fetch(itemId);
        mListener.onPrimaryAction(jasperResource);
    }

    @Override
    public void onActionClick(String itemId) {
        if (mListener == null) return;

        JasperResource jasperResource = mResourceLoader.fetch(itemId);
        mListener.onSecondaryAction(jasperResource);
    }

    @Override
    public void onLoadStarted() {
        getView().showNextLoading();
    }

    @Override
    public void onLoaded(List<JasperResource> jasperResources) {
        getView().hideLoading();
        if (jasperResources.isEmpty()) {
            getView().showEmpty();
        } else {
            getView().showResources(jasperResources);
        }
    }

    @Override
    public void onError(ServiceException ex) {
        mRequestExceptionHandler.showAuthErrorIfExists(ex);
        getView().showError();
    }

    private void reloadResources() {
        getView().clearResources();
        getView().showFirstLoading();
        mResourceLoader.resetSearch();
    }

    public interface ItemSelectListener {
        void onPrimaryAction(JasperResource jasperResource);
        void onSecondaryAction(JasperResource jasperResource);
    }
}
