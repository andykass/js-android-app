/*
 * Copyright © 2015 TIBCO Software, Inc. All rights reserved.
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

package com.jaspersoft.android.jaspermobile.activities.repository;

import android.os.Bundle;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.RepositoryControllerFragment;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.RepositoryControllerFragment_;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboSpiceActivity;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EActivity(R.layout.content_layout)
public class RepositorySearchableActivity extends RoboSpiceActivity {

    @Extra
    String query;
    @Extra
    String resourceUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            RepositoryControllerFragment resourcesController =
                    RepositoryControllerFragment_.builder()
                            .resourceLabel(getString(R.string.search_result_format, query))
                            .resourceUri(resourceUri)
                            .query(query)
                            .build();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.resource_controller, resourcesController, RepositoryControllerFragment.TAG)
                    .commit();
        }
    }

}
