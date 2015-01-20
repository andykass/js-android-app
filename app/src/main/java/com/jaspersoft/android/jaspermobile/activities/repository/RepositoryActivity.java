/*
 * Copyright © 2014 TIBCO Software, Inc. All rights reserved.
 *  http://community.jaspersoft.com/project/jaspermobile-android
 *
 *  Unless you have purchased a commercial license agreement from Jaspersoft,
 *  the following license terms apply:
 *
 *  This program is part of Jaspersoft Mobile for Android.
 *
 *  Jaspersoft Mobile is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Jaspersoft Mobile is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Jaspersoft Mobile for Android. If not, see
 *  <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.activities.repository;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.ResourcesControllerFragment;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.ResourcesControllerFragment_;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.SearchControllerFragment;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.SearchControllerFragment_;
import com.jaspersoft.android.jaspermobile.activities.repository.support.FilterManager;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboActionBarActivity;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;

/**
 * @author Tom Koptel
 * @since 1.9
 */
@EActivity(R.layout.repositories_layout)
public class RepositoryActivity extends RoboActionBarActivity {
    @Bean
    FilterManager filterManager;
    private ResourcesControllerFragment resourcesController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            resourcesController =
                    ResourcesControllerFragment_.builder()
                            .emptyMessage(R.string.r_browser_nothing_to_display)
                            .recursiveLookup(false)
                            .resourceTypes(filterManager.getFiltersByType(FilterManager.Type.ALL_FOR_REPOSITORY))
                            .build();
            transaction.add(R.id.controller, resourcesController, ResourcesControllerFragment.TAG);

            SearchControllerFragment searchControllerFragment =
                    SearchControllerFragment_.builder()
                    .resourceTypes(filterManager.getFiltersByType(FilterManager.Type.ALL_FOR_REPOSITORY))
                    .build();
            transaction.add(searchControllerFragment, SearchControllerFragment.TAG);
            transaction.commit();
        } else {
            resourcesController = (ResourcesControllerFragment)
                    getSupportFragmentManager().findFragmentByTag(ResourcesControllerFragment.TAG);
        }
    }

    @OptionsItem(android.R.id.home)
    final void showHome() {
        super.onBackPressed();
    }


    @OnActivityResult(SearchControllerFragment.SEARCH_ACTION)
    public void searchAction() {
        resourcesController.replacePreviewOnDemand();
    }
}
