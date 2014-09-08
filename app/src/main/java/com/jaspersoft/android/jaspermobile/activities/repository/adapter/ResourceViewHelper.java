/*
 * Copyright (C) 2012 Jaspersoft Corporation. All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of Jaspersoft Mobile for Android.
 *
 * Jaspersoft Mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Jaspersoft Mobile for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.activities.repository.adapter;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.androidannotations.annotations.EBean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EBean
public class ResourceViewHelper {
    private static final String INITIAL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(INITIAL_DATE_FORMAT);

    public void populateView(IResourceView resourceView, ResourceLookup item) {
        switch (item.getResourceType()) {
            case dashboard:
                resourceView.setImageIcon(R.drawable.sample_dashboard_blue);
                break;
            case reportUnit:
                resourceView.setImageIcon(R.drawable.sample_dashboard_blue);
                break;
            default:
                resourceView.setImageIcon(R.drawable.js_blue_gradient);
        }

        resourceView.setTitle(item.getLabel());

        resourceView.setSubTitle(item.getDescription());

        if (item.getResourceType() == ResourceLookup.ResourceType.folder) {
            resourceView.setTimeTamp(formatDateString(item.getCreationDate()));
        }
    }

    private String formatDateString(String updateDate) {
        checkNotNull(updateDate, "Trying to format date with a null String");

        try {
            Date dateValue = dateFormat.parse(updateDate);
            DateFormat dateFormat = DateFormat.getDateInstance();
            return dateFormat.format(dateValue);
        } catch (ParseException ex) {
            return updateDate;
        }
    }

    public void setDateFormat(String template) {
        checkNotNull(template, "Trying to set date format with a null String");
        dateFormat.applyPattern(template);
    }
}
