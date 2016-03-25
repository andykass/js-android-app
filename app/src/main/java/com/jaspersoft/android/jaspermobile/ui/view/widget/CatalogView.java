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

package com.jaspersoft.android.jaspermobile.ui.view.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.jaspermobile.ui.contract.CatalogContract;
import com.jaspersoft.android.jaspermobile.util.resource.JasperResource;
import com.jaspersoft.android.jaspermobile.util.resource.viewbinder.JasperResourceAdapter;
import com.jaspersoft.android.jaspermobile.widget.JasperRecyclerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Andrew Tivodar
 * @since 2.3
 */
@EViewGroup
public abstract class CatalogView extends FrameLayout implements CatalogContract.View, SwipeRefreshLayout.OnRefreshListener, JasperResourceAdapter.OnResourceInteractionListener {

    private final static CatalogContract.EventListener EMPTY = new EmptyEventListener();

    @ViewById(android.R.id.list)
    JasperRecyclerView resourcesList;
    @ViewById(R.id.refreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @ViewById(android.R.id.empty)
    TextView message;

    @Inject
    @Named("THRESHOLD")
    protected int mTreshold;

    protected JasperResourceAdapter mAdapter;
    private CatalogContract.EventListener mEventListener = EMPTY;

    public CatalogView(Context context) {
        super(context);
    }

    public CatalogView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CatalogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEventListener(CatalogContract.EventListener eventListener) {
        mEventListener = eventListener;
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
    public void onRefresh() {
        mEventListener.onRefresh();
    }

    @Override
    public void onResourceItemClicked(JasperResource jasperResource) {
        mEventListener.onItemClick(jasperResource.getId());
    }

    @Override
    public void onSecondaryActionClicked(JasperResource jasperResource) {
        mEventListener.onItemClick(jasperResource.getId());
    }

    private void createDataAdapter() {
        mAdapter = new JasperResourceAdapter(getContext());
        mAdapter.setOnItemInteractionListener(this);
        resourcesList.setAdapter(mAdapter);
        resourcesList.addOnScrollListener(new ScrollListener());
    }

    private static final class EmptyEventListener implements CatalogContract.EventListener {

        @Override
        public void onRefresh() {

        }

        @Override
        public void onScrollToEnd() {

        }

        @Override
        public void onItemClick(String itemId) {

        }

        @Override
        public void onActionClick(String itemId) {

        }
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
                mEventListener.onScrollToEnd();
            }
        }
    }
}
