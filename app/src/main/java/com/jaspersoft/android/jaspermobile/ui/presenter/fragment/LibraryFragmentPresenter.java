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

package com.jaspersoft.android.jaspermobile.ui.presenter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.domain.SimpleSubscriber;
import com.jaspersoft.android.jaspermobile.domain.store.SearchQueryStore;
import com.jaspersoft.android.jaspermobile.ui.presenter.CatalogPresenter;
import com.jaspersoft.android.jaspermobile.ui.presenter.CatalogSearchPresenter;
import com.jaspersoft.android.jaspermobile.ui.view.activity.ToolbarActivity;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.BaseFragment;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.CatalogSearchFragment;
import com.jaspersoft.android.jaspermobile.ui.view.fragment.CatalogSearchFragment_;
import com.jaspersoft.android.jaspermobile.ui.view.widget.LibraryCatalogView;
import com.jaspersoft.android.jaspermobile.ui.view.widget.LibraryCatalogView_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenuItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@EFragment
public class LibraryFragmentPresenter extends BaseFragment {

    private static final String SEARCH_VIEW_TAG = "library_search_view";

    private List<Subscription> mSubscriptionList = new ArrayList<>();

    private LibraryCatalogView catalogView;
    @OptionsMenuItem(R.id.search)
    MenuItem catalogSearchItem;

    @Inject
    CatalogPresenter mCatalogPresenter;
    @Inject
    CatalogSearchPresenter mCatalogSearchPresenter;

    @Inject
    SearchQueryStore mSearchQueryStore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        catalogView = LibraryCatalogView_.build(getActivity());
        return catalogView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @AfterViews
    void init() {
        initCatalog();

        ((ToolbarActivity) getActivity()).setCustomToolbarView(null);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.library_card_label));
        }

        mSubscriptionList.add(mSearchQueryStore.observe().subscribe(new ResourcesObserver()));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        initSearch();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for (Subscription subscription : mSubscriptionList) {
            subscription.unsubscribe();
        }
    }

    private void initCatalog() {
        catalogView.setEventListener(mCatalogPresenter);
        mCatalogPresenter.bindView(catalogView);
    }

    private void initSearch() {
        CatalogSearchFragment catalogSearchFragment = (CatalogSearchFragment) getChildFragmentManager().findFragmentByTag(SEARCH_VIEW_TAG);
        if (catalogSearchFragment == null) {
            catalogSearchFragment = CatalogSearchFragment_.builder().build();
            getChildFragmentManager().beginTransaction().add(catalogSearchFragment, SEARCH_VIEW_TAG).commit();
        }
        catalogSearchFragment.setEventListener(mCatalogSearchPresenter);
        mCatalogSearchPresenter.bindView(catalogSearchFragment);
    }

    private class ResourcesObserver extends SimpleSubscriber<Void> {
        @Override
        public void onNext(Void item) {
            mCatalogPresenter.refresh();
        }
    }
}