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

package com.jaspersoft.android.jaspermobile.ui.view.activity.schedule;

import android.os.Bundle;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.ui.component.activity.ComponentCacheActivity;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.EditScheduleFormFragment;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.EditScheduleFormFragment_;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@EActivity
public class EditScheduleActivity extends ComponentCacheActivity {

    @Extra
    protected int jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            EditScheduleFormFragment fragment = EditScheduleFormFragment_.builder()
                    .jobId(jobId)
                    .build();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, fragment)
                    .commit();
        }
    }

    @Override
    protected String getScreenName() {
        return getString(R.string.ja_edit_sch);
    }
}
