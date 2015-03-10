/*
 * Copyright © 2014 TIBCO Software, Inc. All rights reserved.
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

import android.accounts.Account;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.inject.Inject;
import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.ResourcesControllerFragment;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.ResourcesControllerFragment_;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.SearchControllerFragment;
import com.jaspersoft.android.jaspermobile.activities.repository.fragment.SearchControllerFragment_;
import com.jaspersoft.android.jaspermobile.activities.repository.support.FilterManager;
import com.jaspersoft.android.jaspermobile.activities.repository.support.LibraryPref_;
import com.jaspersoft.android.jaspermobile.activities.repository.support.SortOptions;
import com.jaspersoft.android.jaspermobile.activities.repository.support.SortOrder;
import com.jaspersoft.android.jaspermobile.dialog.FilterDialogFragment;
import com.jaspersoft.android.jaspermobile.dialog.SortDialogFragment;
import com.jaspersoft.android.retrofit.sdk.account.AccountServerData;
import com.jaspersoft.android.retrofit.sdk.account.JasperAccountManager;
import com.jaspersoft.android.sdk.client.JsRestClient;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.List;

import roboguice.fragment.RoboFragment;


/**
 * @author Tom Koptel
 * @since 2.0
 */
@OptionsMenu(R.menu.libraries_menu)
@EFragment
public class LibraryFragment extends RoboFragment {
    public static final String TAG = LibraryFragment.class.getSimpleName();

    @Inject
    protected JsRestClient jsRestClient;

    @Pref
    protected LibraryPref_ pref;
    @Bean
    protected FilterManager filterOptions;
    @Bean
    protected SortOptions sortOptions;

    @OptionsMenuItem
    protected MenuItem filter;
    @OptionsMenuItem
    protected MenuItem sort;

    @InstanceState
    boolean mShowFilterOption;
    @InstanceState
    boolean mShowSortOption;

    private ResourcesControllerFragment resourcesController;
    private SearchControllerFragment searchControllerFragment;

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
            pref.clear();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            resourcesController =
                    ResourcesControllerFragment_.builder()
                            .emptyMessage(R.string.r_browser_nothing_to_display)
                            .resourceTypes(filterOptions.getFilters())
                            .sortOrder(sortOptions.getOrder())
                            .recursiveLookup(true)
                            .build();
            transaction.replace(R.id.resource_controller, resourcesController, ResourcesControllerFragment.TAG + TAG);

            searchControllerFragment =
                    SearchControllerFragment_.builder()
                            .resourceTypes(filterOptions.getFilters())
                            .build();
            transaction.replace(R.id.search_controller, searchControllerFragment, SearchControllerFragment.TAG + TAG);
            transaction.commit();
        } else {
            resourcesController = (ResourcesControllerFragment) getFragmentManager()
                    .findFragmentByTag(ResourcesControllerFragment.TAG + TAG);
            searchControllerFragment = (SearchControllerFragment) getFragmentManager()
                    .findFragmentByTag(SearchControllerFragment.TAG + TAG);
        }

        updateOptionsMenu();
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.h_library_label);
        }
    }

    private void updateOptionsMenu() {
        Account account = JasperAccountManager.get(getActivity()).getActiveAccount();
        AccountServerData accountServerData = AccountServerData.get(getActivity(), account);

        mShowSortOption = true;
        mShowFilterOption = accountServerData.getEdition().equals("PRO");
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        filter.setVisible(mShowFilterOption);
        sort.setVisible(mShowSortOption);
    }

    @OptionsItem(R.id.filter)
    final void startFiltering() {
        FilterDialogFragment.show(getFragmentManager(),
                new FilterDialogFragment.FilterDialogListener() {
                    @Override
                    public void onDialogPositiveClick(List<String> types) {
                        if (resourcesController != null) {
                            resourcesController.loadResourcesByTypes(types);
                        }
                        if (searchControllerFragment != null) {
                            searchControllerFragment.setResourceTypes(types);
                        }
                    }
                });
    }

    @OptionsItem(R.id.sort)
    final void startSorting() {
        SortDialogFragment.show(getFragmentManager(), new SortDialogFragment.SortDialogListener() {
            @Override
            public void onOptionSelected(SortOrder sortOrder) {
                if (resourcesController != null) {
                    resourcesController.loadResourcesBySortOrder(sortOrder);
                }
            }
        });
    }

    @OnActivityResult(SearchControllerFragment.SEARCH_ACTION)
    public void searchAction() {
        resourcesController.replacePreviewOnDemand();
    }

}
