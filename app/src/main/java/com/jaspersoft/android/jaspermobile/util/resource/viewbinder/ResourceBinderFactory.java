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

package com.jaspersoft.android.jaspermobile.util.resource.viewbinder;

import android.content.Context;

import com.jaspersoft.android.jaspermobile.util.resource.JasperResourceType;

/**
 * @author Tom Koptel
 * @since 1.9
 */
public class ResourceBinderFactory {

    private Context mContext;

    public ResourceBinderFactory(Context mContext) {
        this.mContext = mContext;
    }

    public ResourceBinder create(JasperResourceType type) {
        switch (type) {
            case folder:
                return new FolderResourceBinder(mContext);
            case legacyDashboard:
            case dashboard:
                return new DashboardResourceBinder(mContext);
            case report:
                return new ReportResourceBinder(mContext);
            case saved_item:
                return new SavedItemResourceBinder(mContext);
            case file:
                return new FileResourceBinder(mContext);
            case job:
                return new JobResourceBinder(mContext);
            default:
                return new UnknownResourceBinder(mContext);
        }
    }
}
