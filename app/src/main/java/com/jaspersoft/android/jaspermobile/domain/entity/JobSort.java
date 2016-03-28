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

package com.jaspersoft.android.jaspermobile.domain.entity;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
public class JobSort extends Sort {
    public final static String SORT_BY_ID = "sort_by_id";
    public final static String SORT_BY_NAME = "sort_by_name";
    public final static String SORT_BY_REPORT_URI = "sort_by_report_uri";
    public final static String SORT_BY_REPORT_NAME = "sort_by_report_name";
    public final static String SORT_BY_REPORT_FOLDER = "sort_by_report_folder";
    public final static String SORT_BY_OWNER = "sort_by_owner";
    public final static String SORT_BY_STATUS = "sort_by_status";
    public final static String SORT_BY_LAST_RUN = "sort_by_last_run";
    public final static String SORT_BY_NEXT_RUN = "sort_by_next_run";

    public JobSort(String sortType) {
        super(sortType);
    }
}
