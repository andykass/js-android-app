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

package com.jaspersoft.android.jaspermobile.ui.view.viewholder.factory;

import android.content.Context;
import android.view.ViewGroup;

import com.jaspersoft.android.jaspermobile.domain.entity.Resource;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.ResourceViewHolder;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResourceType;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public abstract class ResourceViewHolderFactory<RT extends Resource, ViewHolder extends ResourceViewHolder> {
    protected final Context mContext;

    protected final static int LOADING_ID = -1;

    public ResourceViewHolderFactory(Context mContext) {
        this.mContext = mContext;
    }

    public final int getHolderId(RT resource) {
        if (resource == null) return LOADING_ID;
        return getHolderIdInternal(resource);
    }

    protected abstract int getHolderIdInternal(RT resource);

    public final ViewHolder create(ViewGroup parentView, int viewType) {
        if (viewType == LOADING_ID) return createLoadingViewHolder(parentView);
        return internalCreate(parentView, viewType);
    }

    public abstract ViewHolder internalCreate(ViewGroup parentView, int viewType);

    public abstract ViewHolder createLoadingViewHolder(ViewGroup parentView);
}
