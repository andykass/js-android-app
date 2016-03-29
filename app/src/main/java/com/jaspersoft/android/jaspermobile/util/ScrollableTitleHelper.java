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

package com.jaspersoft.android.jaspermobile.util;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EBean
public class ScrollableTitleHelper {

    @RootContext
    protected ToolbarActivity activity;
    private TextView titleView;

    public void injectTitle(CharSequence title) {
        if (titleView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(activity);
            FrameLayout toolbarCustomView = (FrameLayout) activity.findViewById(R.id.tb_custom);
            View scrollContainer = layoutInflater.inflate(R.layout.scrollable_title_container, toolbarCustomView, false);
            titleView = (TextView) scrollContainer.findViewById(android.R.id.text1);

            activity.setDisplayCustomToolbarEnable(true);
            activity.setCustomToolbarView(scrollContainer);
        }
        titleView.setText(title);
    }

}
