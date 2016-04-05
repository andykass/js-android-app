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

package com.jaspersoft.android.jaspermobile.ui.view.component;

import android.view.ViewGroup;

import com.jaspersoft.android.jaspermobile.domain.entity.Representation;
import com.jaspersoft.android.jaspermobile.domain.entity.Resource;
import com.jaspersoft.android.jaspermobile.domain.model.ResourceModel;
import com.jaspersoft.android.jaspermobile.domain.store.RepresentationStore;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.ui.presenter.resources.ResourcePresenterBinder;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.ResourceViewHolder;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.factory.GridResourceViewHolderFactory;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.factory.ListResourceViewHolderFactory;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.factory.ResourceViewHolderFactory;
import com.jaspersoft.android.jaspermobile.widget.JasperRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerActivity
public class ResourcesAdapter<RT extends Resource, VH extends ResourceViewHolder, RM extends ResourceModel> extends JasperRecyclerView.Adapter<VH> {
    private boolean mIsLoading;
    private List<RT> mResources;

    @Inject
    RepresentationStore mRepresentationStore;
    @Inject
    ListResourceViewHolderFactory<RT, VH> mListResourceViewHolderFactory;
    @Inject
    GridResourceViewHolderFactory<RT, VH> mGridResourceViewHolderFactory;
    @Inject
    ResourcePresenterBinder<RT, VH, RM> mResourcePresenterBinder;
    @Inject
    RM mResourceModel;

    @Inject
    public ResourcesAdapter() {
        mResources = new ArrayList<>();
        setHasStableIds(true);
    }

    public final void setResource(@NotNull List<Resource> resources) {
        mResources = (List<RT>) resources;
        notifyDataSetChanged();
    }

    public final void notifyItemChangedById(int id) {
        for (int i = 0; i < mResources.size(); i++) {
            if (mResources.get(i).getId() == id) {
                notifyItemChanged(i);
            }
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return getCurrentViewHolderFactory().create(parent, viewType);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (getItemViewType(position) == LOADING_TYPE) return;

        mResourcePresenterBinder.bind(holder, mResources.get(position), mResourceModel);
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= mResources.size()) return LOADING_TYPE;

        return getCurrentViewHolderFactory().getHolderId(mResources.get(position));
    }

    @Override
    public int getItemCount() {
        return (mIsLoading && mResources.size() != 0) ? mResources.size() + 1 : mResources.size();
    }

    @Override
    public long getItemId(int position) {
        if (position >= mResources.size()) return -1;

        return mResources.get(position).getId();
    }

    public void showLoading() {
        if (mIsLoading) return;

        mIsLoading = true;
        notifyItemInserted(mResources.size());
    }

    public void hideLoading() {
        if (!mIsLoading) return;

        mIsLoading = false;
        notifyItemRemoved(mResources.size());
    }

    private ResourceViewHolderFactory<RT, VH> getCurrentViewHolderFactory() {
        if (mRepresentationStore.getRepresentationType() == Representation.LIST) {
            return mListResourceViewHolderFactory;
        }

        return mGridResourceViewHolderFactory;
    }
}
