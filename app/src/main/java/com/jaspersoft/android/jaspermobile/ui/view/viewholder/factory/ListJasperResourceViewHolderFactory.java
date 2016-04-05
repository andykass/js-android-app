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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.entity.JasperResource;
import com.jaspersoft.android.jaspermobile.domain.entity.Resource;
import com.jaspersoft.android.jaspermobile.internal.di.ActivityContext;
import com.jaspersoft.android.jaspermobile.internal.di.PerActivity;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.JasperResourceViewHolder;
import com.jaspersoft.android.jaspermobile.ui.view.viewholder.ListReportViewHolder;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResourceType;

import javax.inject.Inject;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@PerActivity
public class ListJasperResourceViewHolderFactory extends ListResourceViewHolderFactory<JasperResource, JasperResourceViewHolder>{

    private final static int REPORT_HOLDER_ID = 1;
    private final static int DASHBOARD_HOLDER_ID = 2;
    private final static int FOLDER_HOLDER_ID = 3;
    private final static int FILE_HOLDER_ID = 4;
    private final static int UNDEFINED_HOLDER_ID = 5;

    @Inject
    public ListJasperResourceViewHolderFactory(@ActivityContext Context mContext) {
        super(mContext);
    }

    @Override
    protected int getHolderIdInternal(JasperResource resource) {
        switch (resource.getType()) {
            case report:
                return REPORT_HOLDER_ID;
            case dashboard:
            case legacyDashboard:
                return DASHBOARD_HOLDER_ID;
            case folder:
                return FOLDER_HOLDER_ID;
            case file:
                return FILE_HOLDER_ID;
            case undefined:
                return UNDEFINED_HOLDER_ID;
            default:
                return UNDEFINED_HOLDER_ID;
        }
    }

    @Override
    public JasperResourceViewHolder internalCreate(ViewGroup parentView, int viewType) {
        switch (viewType) {
            case REPORT_HOLDER_ID:
                View reportResourceView = LayoutInflater.from(mContext).
                        inflate(R.layout.item_list_resource, parentView, false);
                return new ListReportViewHolder(reportResourceView);
            default:
                View defaultResourceView = LayoutInflater.from(mContext).
                        inflate(R.layout.item_list_resource, parentView, false);
                return new ListReportViewHolder(defaultResourceView);
        }
    }

    @Override
    public JasperResourceViewHolder createLoadingViewHolder(ViewGroup parentView) {
        View reportResourceView = LayoutInflater.from(mContext).
                inflate(R.layout.item_resource_list_loading, parentView, false);
        return new JasperResourceViewHolder(reportResourceView);
    }
}
