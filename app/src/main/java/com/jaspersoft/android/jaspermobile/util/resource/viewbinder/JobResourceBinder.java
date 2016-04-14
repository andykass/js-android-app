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
import android.view.View;
import android.widget.ImageView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.jaspermobile.util.resource.JobResource;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Tom Koptel
 * @since 1.9
 */
class JobResourceBinder extends ResourceBinder {

    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public JobResourceBinder(Context context) {
        super(context);
    }

    @Override
    public void setIcon(ImageView imageView, JasperResource jasperResource) {
        imageView.setVisibility(View.GONE);
    }

    @Override
    protected void setActionResource(ResourceView resourceView, JasperResource jasperResource) {
        resourceView.setSecondaryAction(R.drawable.im_cancel);
    }

    @Override
    protected void setSubtitle(ResourceView resourceView, JasperResource item) {
        if (item instanceof JobResource) {
            Date runDate = ((JobResource) item).getDate();
            String runDateString;

            if (runDate != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
                runDateString = simpleDateFormat.format(((JobResource) item).getDate());
            } else {
                runDateString = "--";
            }

            String state = ((JobResource) item).getState();
            String subTitle = getContext().getString(R.string.sch_next_run_label, runDateString) + state;
            resourceView.setSubTitle(subTitle);

            return;
        }

        super.setSubtitle(resourceView, item);
    }

    @Override
    protected void setTitle(ResourceView resourceView, JasperResource item) {
        if (item instanceof JobResource) {
            resourceView.setTitle(item.getLabel());
            return;
        }

        super.setSubtitle(resourceView, item);
    }
}
