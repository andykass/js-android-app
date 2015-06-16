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

package com.jaspersoft.android.jaspermobile.activities.storage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.robospice.RoboToolbarActivity;
import com.jaspersoft.android.jaspermobile.activities.storage.fragment.SavedItemsControllerFragment;
import com.jaspersoft.android.jaspermobile.activities.storage.fragment.SavedItemsControllerFragment_;
import com.jaspersoft.android.jaspermobile.activities.storage.fragment.SavedItemsSearchFragment;
import com.jaspersoft.android.jaspermobile.activities.storage.fragment.SavedItemsSearchFragment_;
import com.jaspersoft.android.jaspermobile.dialog.SortDialogFragment;
import com.jaspersoft.android.jaspermobile.util.filtering.Filter;
import com.jaspersoft.android.jaspermobile.util.filtering.StorageResourceFilter;
import com.jaspersoft.android.jaspermobile.util.sorting.SortOptions;
import com.jaspersoft.android.jaspermobile.util.sorting.SortOrder;
import com.jaspersoft.android.jaspermobile.widget.FilterTitleView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.sharedpreferences.Pref;

import roboguice.fragment.RoboFragment;

/**
 * @author Ivan Gadzhega
 * @author Tom Koptel
 * @since 1.8
 */
@EFragment
@OptionsMenu(R.menu.sort_menu)
public class SavedReportsFragment extends RoboFragment implements SortDialogFragment.SortDialogClickListener{

    private SavedItemsControllerFragment savedItemsController;

    @Bean
    protected StorageResourceFilter storageResourceFilter;
    @Bean
    protected SortOptions sortOptions;

    @Pref
    StoragePref_ pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            // Reset all controls state
            pref.sortType().put(null);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            savedItemsController = SavedItemsControllerFragment_.builder()
                    .sortOrder(sortOptions.getOrder())
                    .build();
            transaction.replace(R.id.resource_controller, savedItemsController, SavedItemsControllerFragment.TAG);

            SavedItemsSearchFragment searchFragment = SavedItemsSearchFragment_.builder().build();
            transaction.replace(R.id.search_controller, searchFragment);

            transaction.commit();
        } else {
            savedItemsController = (SavedItemsControllerFragment) getFragmentManager()
                    .findFragmentByTag(SavedItemsControllerFragment.TAG);
        }

        FilterTitleView filterTitleView = new FilterTitleView(getActivity());
        filterTitleView.init(storageResourceFilter);
        filterTitleView.setFilterSelectedListener(new FilterChangeListener());
        ((RoboToolbarActivity) getActivity()).setDisplayCustomToolbarEnable(true);
        ((RoboToolbarActivity) getActivity()).setCustomToolbarView(filterTitleView);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.sdr_ab_title);
        }
    }

    @OptionsItem(R.id.sort)
    final void startSorting() {
        SortDialogFragment.createBuilder(getFragmentManager())
                .setInitialSortOption(sortOptions.getOrder())
                .setTargetFragment(this)
                .show();
    }

    @Override
    public void onOptionSelected(SortOrder sortOrder) {
        sortOptions.putOrder(sortOrder);
        if (savedItemsController != null) {
            savedItemsController.loadItemsBySortOrder(sortOrder);
        }
    }

    private class FilterChangeListener implements FilterTitleView.FilterListener {
        @Override
        public void onFilter(Filter filter) {
            storageResourceFilter.persist(filter);
            savedItemsController.loadItemsByTypes();
        }
    }
}
