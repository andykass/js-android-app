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

package com.jaspersoft.android.jaspermobile.ui.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.ui.contract.CatalogSearchContract;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@OptionsMenu(R.menu.search_menu)
@EFragment
public class CatalogSearchFragment extends BaseFragment implements CatalogSearchContract.View, SearchView.OnQueryTextListener {

    private final static String SEARCH_QUERY_ARG = "search_query_arg";

    @OptionsMenuItem(R.id.search)
    public MenuItem searchMenuItem;

    private String mQuery;
    private CatalogSearchContract.EventListener mEventListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mQuery = savedInstanceState == null ? "" : savedInstanceState.getString(SEARCH_QUERY_ARG, "");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (!isAdded()) return;

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        if (!mQuery.isEmpty()) {
            searchMenuItem.expandActionView();
        }
        disableSearchViewActionMode(searchView);
        searchView.setQuery(mQuery, false);
        searchView.setQueryHint(getString(R.string.s_hint));
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SEARCH_QUERY_ARG, mQuery);
        super.onSaveInstanceState(outState);
    }

    public void setEventListener(CatalogSearchContract.EventListener eventListener) {
        mEventListener = eventListener;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.equals(mQuery)) return false;

        mQuery = newText;

        if (mEventListener == null) return false;

        mEventListener.onQueryEntered(newText);
        return true;
    }

    private void disableSearchViewActionMode(SearchView searchView) {
        EditText searchInput = (EditText) searchView.findViewById(R.id.search_src_text);
        if (searchInput != null) {
            searchInput.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }
    }
}
