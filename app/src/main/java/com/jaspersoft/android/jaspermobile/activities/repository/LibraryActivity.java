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
package com.jaspersoft.android.jaspermobile.activities.repository;

import android.app.ActionBar;
import android.os.Bundle;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.HomeActivity;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.FilterDialogFragment;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.ResourcesControllerFragment;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.ResourcesControllerFragment_;
import com.jaspersoft.android.sdk.client.oxm.resource.ResourceLookup;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import java.util.ArrayList;
import java.util.List;

import roboguice.activity.RoboFragmentActivity;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EActivity
@OptionsMenu(R.menu.libraries_menu)
public class LibraryActivity extends RoboFragmentActivity {
    private ResourcesControllerFragment resourcesController;
    private static final ArrayList<String> TYPES = new ArrayList<String>() {{
        add(ResourceLookup.ResourceType.reportUnit.toString());
        add(ResourceLookup.ResourceType.dashboard.toString());
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            resourcesController =
                    ResourcesControllerFragment_.builder()
                            .resourceTypes(TYPES).build();
            getSupportFragmentManager().beginTransaction()
                    .add(resourcesController, ResourcesControllerFragment.TAG)
                    .commit();
        } else {
            resourcesController = (ResourcesControllerFragment) getSupportFragmentManager()
                    .findFragmentByTag(ResourcesControllerFragment.TAG);
        }
    }

    @OptionsItem(android.R.id.home)
    final void showHome() {
        HomeActivity.goHome(this);
    }

    @OptionsItem(R.id.filter)
    final void startFiltering() {
        FilterDialogFragment.show(getSupportFragmentManager(),
                new FilterDialogFragment.FilterDialogListener() {
                    @Override
                    public void onDialogPositiveClick(List<String> types) {
                        if (resourcesController != null) {
                            resourcesController.loadResourcesByTypes(types);
                        }
                    }
                });
    }
}
