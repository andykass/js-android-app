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

package com.jaspersoft.android.jaspermobile.activities.save;

import android.os.Bundle;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.save.fragment.SaveItemFragment;
import com.jaspersoft.android.jaspermobile.activities.save.fragment.SaveItemFragment_;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

/**
 * @author Ivan Gadzhega
 * @author Tom Koptel
 * @since 1.8
 */
@EActivity
public class SaveReportActivity extends ToolbarActivity {

    @Extra
    ResourceLookup resource;
    @Extra
    int pageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            SaveItemFragment saveItemFragment = SaveItemFragment_.builder()
                    .resource(resource)
                    .pageCount(pageCount)
                    .build();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, saveItemFragment, SaveItemFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected String getScreenName() {
        return getString(R.string.ja_srs);
    }

}
