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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.activities.schedule.ChooseReportActivity;
import com.jaspersoft.android.jaspermobile.internal.di.modules.activity.FragmentModule;
import com.jaspersoft.android.jaspermobile.ui.contract.LibraryContract;
import com.jaspersoft.android.jaspermobile.ui.presenter.LibraryPresenter;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceAdapter;
import com.jaspersoft.android.jaspermobile.widget.JasperRecyclerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@EFragment(R.layout.fragment_refreshable_resource)
public class LibraryCatalogFragment extends BaseFragment implements LibraryContract.View, SwipeRefreshLayout.OnRefreshListener, JasperResourceAdapter.OnResourceInteractionListener {

    @ViewById(android.R.id.list)
    JasperRecyclerView resourcesList;
    @ViewById(R.id.refreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @ViewById(android.R.id.empty)
    TextView message;
    @OptionsMenuItem(R.id.switchLayout)
    MenuItem switchAction;

    @Inject
    LibraryPresenter libraryPresenter;
    @Inject
    @Named("THRESHOLD")
    protected int mTreshold;

    private JasperResourceAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getProfileComponent()
                .plus(new FragmentModule(this))
                .inject(this);

        libraryPresenter.injectView(this);
    }

    @AfterViews
    void initViews() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.js_blue,
                R.color.js_dark_blue,
                R.color.js_blue,
                R.color.js_dark_blue);

        createDataAdapter();
        libraryPresenter.onReady();
    }

    @Override
    public void onRefresh() {
        libraryPresenter.onRefresh();
    }

    @Override
    public void showResources(List<JasperResource> jasperResourceList) {
        mAdapter.setResources(jasperResourceList);
    }

    @Override
    public void clearResources() {
        mAdapter.clear();
    }

    @Override
    public void showFirstLoading() {
        message.setVisibility(View.VISIBLE);
        message.setText(R.string.loading_msg);
    }

    @Override
    public void showNextLoading() {
        mAdapter.showLoading();
    }

    @Override
    public void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
        message.setVisibility(View.GONE);

        mAdapter.hideLoading();
    }

    @Override
    public void showError() {
        message.setVisibility(View.VISIBLE);
        message.setText(getString(R.string.failed_load_data));
    }

    private void createDataAdapter() {
        mAdapter = new JasperResourceAdapter(getActivity(), true);
        mAdapter.setOnItemInteractionListener(this);
        resourcesList.setAdapter(mAdapter);
        resourcesList.addOnScrollListener(new ScrollListener());
    }

    @Override
    public void onResourceItemClicked(JasperResource jasperResource) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(ChooseReportActivity.RESULT_JASPER_RESOURCE, jasperResource);
        getActivity().setResult(Activity.RESULT_OK, resultIntent);
        getActivity().finish();
    }

    @Override
    public void onSecondaryActionClicked(JasperResource jasperResource) {

    }

    //---------------------------------------------------------------------
    // Implements AbsListView.OnScrollListener
    //---------------------------------------------------------------------

    private class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = recyclerView.getLayoutManager().getItemCount();
            int firstVisibleItem;

            if (layoutManager instanceof LinearLayoutManager) {
                firstVisibleItem = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
            } else {
                firstVisibleItem = ((GridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
            }

            if (totalItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount - mTreshold) {
                libraryPresenter.onScrollToEnd();
            }
        }
    }
}
